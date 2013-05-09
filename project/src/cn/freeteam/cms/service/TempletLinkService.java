package cn.freeteam.cms.service;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.freeteam.base.BaseService;
import cn.freeteam.cms.dao.TempletLinkMapper;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.model.TempletChannel;
import cn.freeteam.cms.model.TempletLink;
import cn.freeteam.cms.model.TempletLinkExample;
import cn.freeteam.cms.model.TempletLinkExample.Criteria;
import cn.freeteam.util.XMLUtil;

/**
 * 
 * <p>Title: TempletLinkService.java</p>
 * 
 * <p>Description: 模板链接相关服务</p>
 * 
 * <p>Date: May 8, 2013</p>
 * 
 * <p>Time: 8:41:17 PM</p>
 * 
 * <p>Copyright: 2013</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @author freeteam
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class TempletLinkService extends BaseService{

	private TempletLinkMapper templetLinkMapper;
	
	public TempletLinkService() {
		initMapper("templetLinkMapper");
	}

	/**
	 * 删除分类
	 * @param id
	 */
	public void delClass(String id){
		if (id!=null && id.trim().length()>0) {
			//先删除子链接
			TempletLinkExample example=new TempletLinkExample();
			Criteria criteria=example.createCriteria();
			criteria.andParidEqualTo(id);
			templetLinkMapper.deleteByExample(example);
			//删除此分类
			templetLinkMapper.deleteByPrimaryKey(id);
			DBCommit();
		}
	}
	/**
	 * 更新
	 * @param templet
	 */
	public void update(TempletLink templetLink){
		templetLinkMapper.updateByPrimaryKey(templetLink);
		DBCommit();
	}
	/**
	 * 添加
	 * @param link
	 * @return
	 */
	public String add(TempletLink templetLink){
		templetLink.setId(UUID.randomUUID().toString());
		templetLinkMapper.insert(templetLink);
		DBCommit();
		return templetLink.getId();
	}
	/**
	 * 检验是否已存在页面标识
	 * @param siteid
	 * @param type
	 * @param isClass
	 * @return
	 */
	public boolean hasPagemark(String templet,String type,boolean isClass,String pagemark){
		TempletLinkExample example=new TempletLinkExample();
		Criteria criteria=example.createCriteria();
		criteria.andTempletEqualTo(templet);
		criteria.andTypeEqualTo(type);
		criteria.andPagemarkEqualTo(pagemark);
		if (isClass) {
			criteria.andSql(" (parid is null or parid = '') ");
		}else {
			criteria.andSql(" (parid is not null and parid != '') ");
		}
		return templetLinkMapper.countByExample(example)>0;
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public TempletLink findById(String id){
		return templetLinkMapper.selectByPrimaryKey(id);
	}
	/**
	 * 查询
	 */
	public List<TempletLink> findAll(TempletLink templetLink,String order){
		TempletLinkExample example=new TempletLinkExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(templetLink, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		return templetLinkMapper.selectByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(TempletLink templetLink,Criteria criteria){
		if (templetLink!=null ) {
			if (templetLink.getTemplet()!=null && templetLink.getTemplet().trim().length()>0) {
				criteria.andTempletEqualTo(templetLink.getTemplet());
			}
			if ("1".equals(templetLink.getIsClass())) {
				criteria.andSql(" (parid is null or parid = '') ");
			}else {
				criteria.andSql(" (parid is not null and parid != '') ");
			}
			if (templetLink.getType()!=null && templetLink.getType().trim().length()>0) {
				criteria.andTypeEqualTo(templetLink.getType());
			}
		}
	}
	/**
	 * 生成指定模板的链接分类初始化数据xml文件
	 * @param templetid
	 */
	public void createXML(Templet templet,HttpServletRequest request){
		if (templet!=null) {
			//查找是否有链接分类数据
			TempletLink templetLink=new TempletLink();
			templetLink.setTemplet(templet.getId());
			templetLink.setIsClass("1");
			List<TempletLink> list=findAll(templetLink, "");
			if (list!=null && list.size()>0) {
				//生成xml
				Document document = DocumentHelper.createDocument();
				//添加元素 links
		        Element linksElement = document.addElement("links");
		        //给links元素添加属性 xmlns:xs="http://www.w3.org/2001/XMLSchema"
		        linksElement.addAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
		        for (int i = 0; i < list.size(); i++) {
		        	templetLink=list.get(i);
			        //添加元素link
			        Element linkElement = linksElement.addElement("link");
			        if (templetLink.getId()!=null) {
				        //添加link子元素 id
				        Element idElement = linkElement.addElement("id");
				        idElement.setText(templetLink.getId());
			        }
			        if (templetLink.getName()!=null) {
				        //添加link子元素 name
				        Element nameElement = linkElement.addElement("name");
				        nameElement.setText(templetLink.getName());
			        }
			        if (templetLink.getTemplet()!=null) {
				        //添加link子元素 templet
				        Element templetElement = linkElement.addElement("templet");
				        templetElement.setText(templetLink.getTemplet());
			        }
			        if (templetLink.getImg()!=null) {
				        //添加link子元素 img
				        Element imgElement = linkElement.addElement("img");
				        imgElement.setText(templetLink.getImg());
			        }
			        if (templetLink.getParid()!=null) {
				        //添加link子元素 parid
				        Element paridElement = linkElement.addElement("parid");
				        paridElement.setText(templetLink.getParid());
			        }
			        if (templetLink.getUrl()!=null) {
				        //添加link子元素 url
				        Element urlElement = linkElement.addElement("url");
				        urlElement.setText(templetLink.getUrl());
			        }
			        if (templetLink.getOrdernum()!=null) {
				        //添加link子元素 ordernum
				        Element ordernumElement = linkElement.addElement("ordernum");
				        ordernumElement.setText(""+templetLink.getOrdernum());
			        }
			        if (templetLink.getPagemark()!=null) {
				        //添加link子元素 pagemark
				        Element pagemarkElement = linkElement.addElement("pagemark");
				        pagemarkElement.setText(templetLink.getPagemark());
			        }
			        if (templetLink.getIsok()!=null) {
				        //添加link子元素 isok
				        Element isokElement = linkElement.addElement("isok");
				        isokElement.setText(templetLink.getIsok());
			        }
			        if (templetLink.getType()!=null) {
				        //添加link子元素 type
				        Element typeElement = linkElement.addElement("type");
				        typeElement.setText(templetLink.getType());
			        }
				}
		        //生成xml文件
		        XMLUtil.writeFile(document, request.getRealPath("/")+"/templet/"+templet.getId()+"/links.xml");
			}
		}
	}

	public TempletLinkMapper getTempletLinkMapper() {
		return templetLinkMapper;
	}

	public void setTempletLinkMapper(TempletLinkMapper templetLinkMapper) {
		this.templetLinkMapper = templetLinkMapper;
	}
}