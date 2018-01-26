package controller;

import com.java1234.model.Equipment;
import com.java1234.model.EquipmentType;
import com.java1234.model.PageBean;
import com.java1234.model.User;
import com.java1234.service.EquipmentService;
import com.java1234.service.EquipmentTypeService;
import util.PageUtil;
import util.ResponseUtil;
import util.StringUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/equipment")
public class EquipmentController {

	@Resource(name = "equipmentService")
	private EquipmentService equipmentService;

	@Resource(name = "equipmentTypeService")
	private EquipmentTypeService equipmentTypeService;
	
	@RequestMapping("/list")
	public ModelAndView list(@RequestParam(value="page",required=false)String page,Equipment s_equipment,HttpServletRequest request){
		ModelAndView mav=new ModelAndView();
		HttpSession session=request.getSession();
		if(StringUtil.isEmpty(page)){
			page="1";
			session.setAttribute("s_equipment", s_equipment);
		}else{
			s_equipment=(Equipment) session.getAttribute("s_equipment");
		}
		PageBean pageBean=new PageBean(Integer.parseInt(page),10);
		List<Equipment> equipmentList=equipmentService.find(pageBean, s_equipment);
		int total=equipmentService.count(s_equipment);
		String pageCode=PageUtil.getPagation(request.getContextPath()+"/equipment/list.do", total, Integer.parseInt(page), 10);
		mav.addObject("pageCode", pageCode);
		mav.addObject("modeName", "设备管理");
		mav.addObject("equipmentList", equipmentList);
		mav.addObject("mainPage", "equipment/list.jsp");
		mav.setViewName("main");
		return mav;
	}


	@RequestMapping("/delete")
	public void delete(@RequestParam(value="id")String id,HttpServletResponse response)throws Exception{
		JSONObject result=new JSONObject();
		equipmentService.delete(Integer.parseInt(id));
		result.put("success", true);
		ResponseUtil.write(result, response);
	}


	@RequestMapping("/preSave")
	public ModelAndView preSave(@RequestParam(value="id",required=false)String id){
		ModelAndView mav=new ModelAndView();
		List<EquipmentType> equipmentTypeList=equipmentTypeService.find(null, null);
		mav.addObject("mainPage", "equipment/save.jsp");
		mav.addObject("modeName", "设备管理");
		mav.addObject("equipmentTypeList",equipmentTypeList);
		mav.setViewName("main");
		if(StringUtil.isNotEmpty(id)){
			mav.addObject("actionName", "设备修改");
			Equipment equipment=equipmentService.loadById(Integer.parseInt(id));
			mav.addObject("equipment", equipment);
		}else{
			mav.addObject("actionName", "设备添加");
		}
		return mav;
	}

	@RequestMapping("/save")
	public String save(Equipment equipment){
		if(equipment.getId()==null){
			equipmentService.add(equipment);
		}else{
			equipmentService.update(equipment);
		}
		return "redirect:/equipment/list.do";
	}

	@RequestMapping("/userList")
	public ModelAndView userList(@RequestParam(value="page",required=false)String page,Equipment s_equipment,HttpServletRequest request){
		ModelAndView mav=new ModelAndView();
		HttpSession session=request.getSession();
		if(StringUtil.isEmpty(page)){
			page="1";
			session.setAttribute("s_equipment", s_equipment);
		}else{
			s_equipment=(Equipment) session.getAttribute("s_equipment");
		}
		PageBean pageBean=new PageBean(Integer.parseInt(page),3);
		List<Equipment> equipmentList=equipmentService.find(pageBean, s_equipment);
		int total=equipmentService.count(s_equipment);
		String pageCode=PageUtil.getPagation(request.getContextPath()+"/equipment/userList.do", total, Integer.parseInt(page), 3);
		mav.addObject("pageCode", pageCode);
		mav.addObject("modeName", "使用设备管理");
		mav.addObject("equipmentList", equipmentList);
		mav.addObject("mainPage", "equipment/userList.jsp");
		mav.setViewName("main");
		return mav;
	}
	
	
	@RequestMapping("/repair")
	public void repair(@RequestParam(value="id")String id,HttpSession session,HttpServletResponse response)throws Exception{
		String userMan=((User)session.getAttribute("currentUser")).getUserName();
		JSONObject result=new JSONObject();
		equipmentService.addRepair(Integer.parseInt(id), userMan);
		result.put("success", true);			
		ResponseUtil.write(result, response);
	}
	
}
