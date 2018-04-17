package com.range.shipon.mybatis.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.range.shipon.mybatis.mapper.ProductMapper;
import com.range.shipon.mybatis.model.Comparing;
import com.range.shipon.mybatis.model.Product;

@Service
public class ComparingService {

	@Autowired
	private ProductMapper mapper;

	public ArrayList<Comparing> getComparingList() throws Exception {
		return mapper.getComparingList();
	}

	public ArrayList<Product> getComparingItem(long seq) throws Exception {
		return mapper.getComparingItem(seq);
	}

	public void updateComparingItemTitle(long seq, String title) throws Exception {
		mapper.updateComparingItemTitle(seq, title);
	}
	
	public void removeComparingItem(long seq) throws Exception {
		mapper.removeComparing(seq);
		mapper.removeComparingItem(seq);
	}

	public ArrayList<Product> getSearchResult(String queryId, String queryName) throws Exception {
		return mapper.getProductList(null, queryId, queryName, "mallId", 1, -1);
	}

	public void addComparing(String title, long[] ids) throws Exception {
		mapper.addComparing(title);
		for (int i = 0; i < ids.length; i++) {
			mapper.addComparingItem(ids[i]);
		}
	}

	public Comparing getLastComparing() throws Exception {
		return mapper.getLastComparing();
	}

 }
