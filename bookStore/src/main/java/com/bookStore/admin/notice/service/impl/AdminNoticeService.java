package com.bookStore.admin.notice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookStore.admin.notice.dao.IAdminNoticeDao;
import com.bookStore.admin.notice.service.IAdminNoticeService;
import com.bookStore.commons.beans.Notice;
import com.bookStore.utils.PageModel;

@Service
public class AdminNoticeService implements IAdminNoticeService {
	
	@Autowired
	private IAdminNoticeDao adminNoticeDao;

	@Override
	public int findListNoticeCount() {
		return adminNoticeDao.selectListNoticeCount();
	}
	
	@Override
	public List<Notice> findListNotice(PageModel pagemodel) {
		Map map = new HashMap();
		map.put("start", pagemodel.getFirstLimitParam());
		map.put("pageSize", pagemodel.getPageSize());
		return adminNoticeDao.selectListNotice(map);
	}

	@Override
	public int addNotice(Notice notice) {
		return adminNoticeDao.insertNotice(notice);
	}

	@Override
	public Notice findNoticeById(Integer id) {
		return adminNoticeDao.selectNoticeById(id);
	}

	@Override
	public int modifyNotice(Notice notice) {
		return adminNoticeDao.updateNotice(notice);
	}

	@Override
	public int removeNoticeById(Integer id) {
		return adminNoticeDao.deleteNoticeById(id);
	}


}
