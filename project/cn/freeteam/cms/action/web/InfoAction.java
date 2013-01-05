package cn.freeteam.cms.action.web;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Info;
import cn.freeteam.cms.service.InfoService;


/** 
 * <p>Title: InfoAction.java</p>
 * 
 * <p>Description: InfoAction</p>
 * 
 * <p>Date: May 22, 2012</p>
 * 
 * <p>Time: 8:46:48 PM</p>
 * 
 * <p>Copyright: 2011</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @author 王浩强
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class InfoAction extends BaseAction{

	private InfoService infoService;
	
	private Info info;
	
	public InfoAction(){
		init("infoService");
	}

	/**
	 * ajax点击
	 * @return
	 */
	public String ajaxClick(){
		int clicknum=0;
		if (info!=null && info.getId()!=null && info.getId().trim().length()>0) {
			info=infoService.findById(info.getId());
			if (info!=null) {
				info.setClicknum((info.getClicknum()!=null?info.getClicknum():0)+1);
				infoService.click(info);
				clicknum=info.getClicknum();
			}
		}
		write(""+clicknum, "UTF-8");
		return null;
	}
	public InfoService getInfoService() {
		return infoService;
	}

	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}
}
