package com.bookStore.admin.product.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bookStore.admin.product.service.IAdminProductService;
import com.bookStore.commons.beans.Product;
import com.bookStore.commons.beans.ProductList;
import com.bookStore.utils.IdUtils;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/admin/products")
public class AdminProductHandler {
	
	@Autowired
	private IAdminProductService adminProductService;
	
	@RequestMapping("/listProduct.do")
	public String listProduct(Model model){
		List<Product> products = adminProductService.findProduct();
		model.addAttribute("products", products);
		
		return "/admin/products/list.jsp";
	}
	
	@RequestMapping("/findProductByManyCondition.do")
	public String findProductByManyCondition(Product product, String minprice, String maxprice, Model model){
		List<Product> products = adminProductService.findProductByManyCondition(product, minprice, maxprice);
		model.addAttribute("products", products);
		model.addAttribute("product", product);
		model.addAttribute("minprice", minprice);
		model.addAttribute("maxprice", maxprice);
		
		return "/admin/products/list.jsp";
	}
	
	@RequestMapping("/addProduct.do")
	public String addProduct(MultipartFile upload, Product product, HttpServletRequest request) throws IllegalStateException, IOException{
		
		String path = request.getSession().getServletContext().getRealPath("/productImg");
		File file = new File(path);
		if(!file.exists()){        //file.exists(), 如果路径存在返回true
			file.mkdirs();
		}
		String filename = IdUtils.getUUID() + "-" +upload.getOriginalFilename();
		String imgurl = path + File.separatorChar + filename;
		upload.transferTo(new File(imgurl));
		product.setId(IdUtils.getUUID());
		product.setImgurl("/productImg/" + filename);
		
		int count = adminProductService.addProduct(product);
		return "/admin/products/listProduct.do";
		
	}
	
	@RequestMapping("/findProductById.do")
	public String findProductById(String id, Model model){
		Product product = adminProductService.findProductById(id);
		
		model.addAttribute("p", product);
		
		return "/admin/products/edit.jsp";
		
	}
	
	@RequestMapping("/editProduct.do")
	public String editProduct(Product product, MultipartFile upload, HttpSession session) throws IllegalStateException, IOException{
		if(!upload.isEmpty()){
			String path = session.getServletContext().getRealPath("/productImg");
			Product target = adminProductService.findProductById(product.getId());
			File targetfile = new File(session.getServletContext().getRealPath("/") + target.getImgurl());
			if(targetfile.exists()){
				targetfile.delete();
			}
			String filename = IdUtils.getUUID() + "-" + upload.getOriginalFilename();
			String imgurl = path + File.separatorChar + filename;
			upload.transferTo(new File(imgurl));
			product.setImgurl("/productImg/" + filename);
		}
		int count = adminProductService.modifyProduct(product);
		return "/admin/products/listProduct.do";
	}
	
	@RequestMapping("/deleteProduct.do")
	public String deleteProduct(String id, HttpSession session){
		Product product = adminProductService.findProductById(id);
	    File targetfile = new File(session.getServletContext().getRealPath("/") + product.getImgurl());
	    if(targetfile.exists()){
	    	targetfile.delete();
	    }
	    int count = adminProductService.removeProduct(id);
	    return "/admin/products/listProduct.do";
	}
	
	@RequestMapping("/findProductByTime.do")
	@ResponseBody
	public void findProductByTime(String year, String month, Model model, HttpServletResponse response) throws IOException{
		List<ProductList> plist = adminProductService.findProductList(year, month);
		PrintWriter out=response.getWriter();       //向客户端发送字符数据
		response.setContentType("text/text");          //设置请求以及响应的内容类型以及编码方式
		response.setCharacterEncoding("UTF-8");
		JSONArray json = JSONArray.fromObject(plist);  //将newsList对象转换为json对象
		String str = json.toString();
		out.write(str);
		
	}
	
	@RequestMapping("/download.do")
	public void download(String year, String month, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		/*List<ProductList> plist = adminProductService.findProductList(year, month);
		String filename;
		if("0".equals(month)){
			filename = year +"年" + "销售榜单.csv";
		}else{
			filename = year +"年" + month + "月销售榜单.csv";
		}
		response.setHeader("Content-Disposition", "attachment;filename="+processFileName(request, filename));
		response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("gbk"),"ISO-8859-1"));  
		response.setContentType(session.getServletContext().getMimeType(filename));
		response.setCharacterEncoding("GBK");
		PrintWriter out = response.getWriter();
	    out.println("商品名称,商品销量");
	    for(int i=0; i<plist.size(); i++){
	    	ProductList pl = plist.get(i);
	    	out.println(pl.getName() + "," + pl.getSalnum());
	    }
	    out.flush();
	    out.close();*/
		
		List<ProductList> plist = adminProductService.findProductList(year, month);
		String filename;
		String sheetname;
		String titlename;
		if("0".equals(month)){
			filename = year + "年销售榜单.xls";
			sheetname = year + "年销售榜单";
			titlename = year + "年销售榜单";
		}else{
			filename = year + "年" + month + "月销售榜单.xls";
			sheetname = month + "月销售榜单";
			titlename = year + "年" + month + "月销售榜单";
		}
		 
		String[]columnName = {"书名", "销售量"};
		
		int columnNum = 2;
		
		String[][] dataList = new String [plist.size()][columnNum];
		for(int i=0; i<plist.size(); i++){
			ProductList pl = plist.get(i);
			dataList[i][0] = pl.getName();
			dataList[i][1] = pl.getSalnum();
		}
		
		//对应创建一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		//创建一个sheet
		HSSFSheet sheet = wb.createSheet(sheetname);
		//创建第一行
		HSSFRow row1 = sheet.createRow(0);
		//创建单元格
		HSSFCell cell1 = row1.createCell(0);
		//合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
		cell1.setCellValue(titlename);
		
		//创建第二行
		HSSFRow row2 = sheet.createRow(1);
		for(int i=0; i<columnNum; i++){
			row2.createCell(i);
			HSSFCell cell = row2.createCell(i);
			cell.setCellValue(columnName[i]);
		}
		
		//创建数据行
				for(int i=0;i<dataList.length;i++){
					row2 = sheet.createRow(i+2);
					HSSFCell datacell=null;
					for(int j=0;j<columnNum;j++)
					{
						datacell=row2.createCell(j);
						datacell.setCellValue(dataList[i][j]);
					}
				}
				
				//文件下载
				
				response.setContentType("application/ms-excel;charset=utf-8");
				response.setHeader("content-Disposition", "attachment;filename="+processFileName(request,filename));
				OutputStream out = response.getOutputStream();
				wb.write(out);
	}

	
	
	
	
	
	public String processFileName(HttpServletRequest request, String fileNames){
		String codedFilename = null;
		try{
			String agent = request.getHeader("USER-AGENT");
			if(null != agent && -1 != agent.indexOf("MSIE") || null != agent && -1 !=agent.indexOf("Trident")){  //IE
				String name = java.net.URLEncoder.encode(fileNames, "UTF-8");
				
				codedFilename = name;
			}else if(null != agent && -1 != agent.indexOf("Mozilla")){  //火狐，Chrome等
				codedFilename = new String(fileNames.getBytes("UTF-8"), "iso-8859-1");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return codedFilename;
	}
	
	
	
	
	
	
	
	
	
}
