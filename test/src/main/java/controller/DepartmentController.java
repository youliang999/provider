package controller;

import com.java1234.model.Department;
import com.java1234.model.PageBean;
import com.java1234.model.User;
import com.java1234.service.DepartmentService;
import com.java1234.service.UserService;
import controller.enums.DataBindTypeEnum;
import databind.DataBind;
import databind.DataBindManager;
import databind.SessionContext;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import util.PageUtil;
import util.ResponseUtil;
import util.StringUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/department")
public class DepartmentController {
	private static final Logger LOGGER = Logger.getLogger(UserController.class);
	private static final DataBind<User> LOGIN_USER_BIND = DataBindManager.getInstance().getDataBind(DataBindTypeEnum.LOGIN_USER);
	private final SessionContext sessionContext = SessionContext.getInstance();
	@Resource(name = "departmentService")
	private DepartmentService departmentService;

	@Resource(name = "userService")
	private UserService userService;
	
	@RequestMapping("/list")
	public ModelAndView list(@RequestParam(value="page",required=false)String page,Department s_department,HttpServletRequest request){
		ModelAndView mav=new ModelAndView();
		HttpSession session=request.getSession();
		if(StringUtil.isEmpty(page)){
			page="1";
			session.setAttribute("s_department", s_department);
		}else{
			s_department=(Department) session.getAttribute("s_department");
		}
		User current = LOGIN_USER_BIND.get();
		User current1 = sessionContext.get();
		if(current != null) {
			LOGGER.info("name1111:" + current.getUserName());
		}
		if(current1 != null) {
			LOGGER.info("name2222:" + current1.getUserName());
		}
		PageBean pageBean=new PageBean(Integer.parseInt(page),10);
		List<Department> departmentList=departmentService.find(pageBean, s_department);
		int total=departmentService.count(s_department);
		String pageCode=PageUtil.getPagation(request.getContextPath()+"/department/list.do", total, Integer.parseInt(page), 10);
		mav.addObject("pageCode", pageCode);
		mav.addObject("modeName", "部门管理");
		mav.addObject("departmentList", departmentList);
		mav.addObject("mainPage", "department/list.jsp");
		mav.setViewName("main");
		return mav;
	}

	@RequestMapping("/preSave")
	public ModelAndView preSave(@RequestParam(value="id",required=false)String id){
		ModelAndView mav=new ModelAndView();
		mav.addObject("mainPage", "department/save.jsp");
		mav.addObject("modeName", "部门管理");
		mav.setViewName("main");
		if(StringUtil.isNotEmpty(id)){
			mav.addObject("actionName", "部门修改");
			Department department=departmentService.loadById(Integer.parseInt(id));
			mav.addObject("department", department);
		}else{
			mav.addObject("actionName", "部门添加");
		}
		return mav;
	}

	@RequestMapping("/save")
	public String save(Department department){
		if(department.getId()==null){
			departmentService.add(department);
		}else{
			departmentService.update(department);
		}
		return "redirect:/department/list.do";
	}

	@RequestMapping("/delete")
	public void delete(@RequestParam(value="id")String id,HttpServletResponse response)throws Exception{
		JSONObject result=new JSONObject();
		if(userService.existUserByDeptId(Integer.parseInt(id))){
			result.put("errorInfo", "该部门下存在用户，不能删除！");
		}else{
			departmentService.delete(Integer.parseInt(id));
			result.put("success", true);			
		}
		ResponseUtil.write(result, response);
	}
}
