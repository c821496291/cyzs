package com.org.cygs.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.org.cygs.pojo.CheckUnit;
import com.org.cygs.pojo.Job;
import com.org.cygs.pojo.Page;
import com.org.cygs.pojo.Contract;
import com.org.cygs.pojo.PagePojo;
import com.org.cygs.pojo.Part;
import com.org.cygs.pojo.PartPosition;
import com.org.cygs.pojo.PrIndexPC;
import com.org.cygs.pojo.Project;
import com.org.cygs.pojo.Unit;
import com.org.cygs.service.CheckUnitService;
import com.org.cygs.service.ContractService;
import com.org.cygs.service.JobService;
import com.org.cygs.service.PartPositionService;
import com.org.cygs.service.PartService;
import com.org.cygs.service.ProjectService;
import com.org.cygs.service.TimePriceService;
import com.org.cygs.service.YUnitService;
import com.org.cygs.util.common.StringUtil;


@Controller
@RequestMapping("/contract")
public class ContractController {
	@Autowired
	private ContractService  contractService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private PartService PartService;
	@Autowired
	private PartPositionService partPostionService;
	@Autowired
	private YUnitService unitService;
	@Autowired
	private JobService JobService;
	@Autowired
	private CheckUnitService checkUnitService;
	@Autowired
	private TimePriceService timePriceService;
	//转到合同单价查看页面
	@RequestMapping("/contractList")
	public String contractList(HttpServletRequest request,Model model){
		List<Project> prList=projectService.getAllProjectName();
		List<CheckUnit> cuList=checkUnitService.getAllCheckUnit();
		List<Part>  partList=PartService.getAllPart();
		model.addAttribute("prList", prList);
		model.addAttribute("cuList", cuList);
		model.addAttribute("partList", partList);
		System.out.println("contractList");
		return "listPrice";
	}
	//动态变更工程部位选项
	@RequestMapping("/changePosition")
	public @ResponseBody String changePosition(@RequestBody String pId ) throws JsonParseException, JsonMappingException, IOException{
		
		System.out.println(pId+"onChange");
		ObjectMapper mapper=new ObjectMapper();
		Contract contract=mapper.readValue(pId, Contract.class);
		
		String id=contract.getpId();
		System.out.println(id+"onChange");
		//List<PartPosition> pList=PartService.getPartPositionByPId(id);
		List<PartPosition> pList= partPostionService.selectPartPositionByPId(id);
		System.out.println(pList.size()+"123");
		String jsonList=new String();
		if(pList.size()>0){
		jsonList=mapper.writeValueAsString(pList);
		}
		
		return jsonList;	
	}
	//动态变更工作项目选项
	@RequestMapping("/changeJob")
	public @ResponseBody String changeJob(@RequestBody String psId ) throws JsonParseException, JsonMappingException, IOException{
		
		System.out.println(psId+"onChange");
		ObjectMapper mapper=new ObjectMapper();
		Contract contract=mapper.readValue(psId, Contract.class);
		
		String id=contract.getPsId();
		System.out.println(id+"onChange");
		List<Job> jList=JobService.selectJobByPsId(id);
		System.out.println(jList.size()+"123");
		String jsonList=new String();
		if(jList.size()>0){
		jsonList=mapper.writeValueAsString(jList);
		}
		
		return jsonList;		
	}
	//动态变更栋号选项
	@RequestMapping("/changeUnitNumber")
	public @ResponseBody String changeUnitNumber(@RequestBody String prId ) throws JsonParseException, JsonMappingException, IOException{
		
		System.out.println(prId+"onChange");
		ObjectMapper mapper=new ObjectMapper();
		Contract contract=mapper.readValue(prId, Contract.class);
		
		String id=contract.getPrId();
		System.out.println(id+"onChange");
		List<PrIndexPC> pList=projectService.getProjectDetailById(id);
		System.out.println(pList.get(0).getPcPId().toString());
		System.out.println(pList.size());
		
		String jsonList=new String();
		if(pList.size()>0){
		jsonList=mapper.writeValueAsString(pList);
		System.out.println(jsonList);
		}
		
		return jsonList;		
	}
	
	//动态变更分部选项
	@RequestMapping("/changePartPosition")
	public @ResponseBody List<PartPosition> changePartPosition(@RequestBody Map<String, Object> map) throws JsonParseException, JsonMappingException, IOException{
		System.out.println("changePosition");
		System.out.println(map.toString()+"changePosition");
		Part part=new Part();
		part.setPrId((String)map.get("prId"));
		part.setpName((String)map.get("pName"));
		System.out.println(part.toString()+"onChange2");		
		List<PartPosition> psList=new ArrayList<PartPosition>();
		if (part.getPrId()!=null && !part.getPrId().equals("")&&part.getpName()!=null && !part.getpName().equals("")) {
			psList=PartService.getPsNameByPart(part);
		}
		
		System.out.println(psList.size());
	
		return psList;		
	}
	//更改单位
	@RequestMapping("/changeUnitName")
	public @ResponseBody String changeUnitName(@RequestBody String jobKey) throws JsonParseException, JsonMappingException, IOException{
		
		System.out.println(jobKey+"onChange");
		ObjectMapper mapper=new ObjectMapper();
		Contract contract=mapper.readValue(jobKey, Contract.class);
		
		String id=contract.getJobKey();
		System.out.println(id+"onChange");
		Job job=JobService.selectJob(id);
		
		System.out.println(job.toString()+"I am here");
		String unId=job.getUnId();
		Unit unit=unitService.getUnitById(unId);
		System.out.println(unit.toString());
		
		String jsonList=mapper.writeValueAsString(unit);
		
		return jsonList;		
	}
	//转到合同单价管理页面
	@RequestMapping("/managePrice")
	public String managePrice(HttpServletRequest request,Model model){
		List<Project> prList=projectService.getAllProjectName();
		List<CheckUnit> cuList=checkUnitService.getAllCheckUnit();
		List<Part>  partList=PartService.getAllPart();
		model.addAttribute("prList", prList);
		model.addAttribute("cuList", cuList);
		model.addAttribute("partList", partList);
		System.out.println("manageContract");
		return "managePrice";
	}

	@RequestMapping("/selectContract")
	public @ResponseBody Page<Contract> selectContract1(@RequestBody Map<String, Object> map,HttpSession session){
		
		
		System.out.println("selectContract");
		Contract contract=new Contract();
		contract.setPrId((map.get("prId").toString()));
	
	
		System.out.println(contract.toString());
		Page<Contract> pagePrice=contractService.selectContractList(map);
		return pagePrice;
	}
	
	@RequestMapping("/selectContract2")
	public @ResponseBody Map<String, Object> selectContract2(@RequestParam Map<String, Object> map,HttpSession session){
		
		
		return contractService.selectPrice(map);
	}
	
	@RequestMapping("/selectContract1")
	public @ResponseBody PagePojo selectContract(@RequestBody Map<String, String> map) {
		System.out.println("hello");
		Contract contract = new Contract();
		contract.setPrName((map.get("prName").toString()));
		contract.setpName((map.get("pName").toString()));
		contract.setPsName((map.get("psName").toString()));
		contract.setJobName((map.get("jobName").toString()));
		contract.setFsName((map.get("fsName").toString()));
		contract.setPcpNumber((map.get("pcpNumber").toString()));
		// contract.setUnName((map.get("unName").toString()));
		contract.setCuName((map.get("cuName").toString()));
		contract.setPrice(Float.parseFloat(map.get("price").toString()));
		int pageNo = Integer.parseInt(map.get("pageNo"));
		PagePojo<Contract> pageContract = contractService.selectContract(pageNo, 15, contract);
		System.out.println(pageContract.getSize());
		return pageContract;

	}
    //转到修改合同单价页面
	@RequestMapping("/toEditContract/{opId}")
	public String toEditContract(@PathVariable("opId") String opId,Model model){
		Contract contract=contractService.selectContractByOpId(opId);
		model.addAttribute("contract", contract);
		System.out.println(contract.toString());
		return "editContract";
	}
	//修改合同单价
	@RequestMapping("/editContract")
	public @ResponseBody String editContract(HttpServletRequest request){
		Contract contract=new Contract();
		contract.setOpId(request.getParameter("opId"));
		contract.setPrice(Float.parseFloat(request.getParameter("price")));
		try {
			contractService.updateContract(contract);
		} catch (Exception e) {
			e.printStackTrace();
			return "0";// TODO: handle exception
		}		
		return "1";
	}
	//批量删除合同单价
	@RequestMapping("/deletePrice")
	public @ResponseBody String deletePrice(@RequestBody String[] ids){
		System.out.println("deletePrice");
		System.out.println(ids.length);
		try {
			for(String opId:ids){
				System.out.println(opId);
				contractService.deleteContract(opId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";// TODO: handle exception
		}						
		return "1";		
	}
	
	//转到添加合同单价
	@RequestMapping("/toAddPrice")
	public String toAddPrice(Model model){
		List<Project> prList=projectService.getAllProjectName();
		List<CheckUnit> cuList=checkUnitService.getAllCheckUnit();
		List<Part>  partList=PartService.getAllPart();
		List<Unit>  unList=unitService.getAllUnit();
		
		model.addAttribute("prList", prList);
		model.addAttribute("cuList", cuList);
		model.addAttribute("partList", partList);
		model.addAttribute("unList", unList);
		System.out.println("toaddPrice");
		return "addPrice";
	}
	
	//添加合同单价
	@RequestMapping("addPrice")
	public @ResponseBody String addPrice(HttpServletRequest request){
		System.out.println("addPrice");
	
		
		BigDecimal budgetSum=new BigDecimal(999999.00);
	
		String prId=(String)request.getParameter("prId");
		String[] pcpIds=request.getParameterValues("pcpIdIsSelect");
		String pId=(String)request.getParameter("pId");
		String psId=(String)request.getParameter("psId");
		String unId=(String)request.getParameter("unId");
		String cuId=(String)request.getParameter("cuId");
		Float price=Float.parseFloat(request.getParameter("price"));
		String jobKey=(String)request.getParameter("jobKey");
		
		
		//Contract contract=new Contract();
		//contract.setPrId(prId);
		//contract.setPsId(psId);
		//contract.setCuId(cuId);
		//contract.setUnId(unId);
		//contract.setPrice(price);
		//contract.setJobKey(jobKey);
		//contract.setIsbudget(isbudget);
		//contract.setBudgetSum(budgetSum);
		try {				
								
					Contract contract=new Contract();					
					contract.setPrId(prId);
					contract.setCuId(cuId);	
					contract.setUnId(unId);													
					contract.setpId(pId);
					contract.setPsId(psId);
					contract.setJobKey(jobKey);
					
					
				for(String pcpId:pcpIds){
					contract.setPcpId(pcpId);
					List<Contract> cList=contractService.selectContracts(contract);
					System.out.println(cList.size());
					if (cList.size() == 0) { // 该单价没有重复，新建一个单价																																													
						contract.setPrice(price);
						contract.setIsbudget(1);//新合同单价
						contract.setBudgetSum(budgetSum);
						System.out.println(contract.toString());
						contractService.addContract(contract);
						
					}else{// 该单价有重复，赋新的price值
						contract = cList.get(0);
						contract.setPrice(price);
						contractService.updateContract(contract);
						
					}				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";// TODO: handle exception
		}				
		return "1";
	}
	
    //转到批量导入单价
	@RequestMapping("/toImportPrice")
	public String toImportPrice(HttpServletRequest request,Model model){
		String filename=request.getParameter("filename");
		model.addAttribute("filename", filename);
		return "importPrice";
	}
	
	//批量导入单价
	@RequestMapping("importPrice")
	public String importPrice(HttpServletRequest request){
		return "redirect:/contract/contractList";
	}
	


    
    @RequestMapping("/toPriceAndYkl")
    public String toPriceAndYkl(HttpServletRequest request,Model model){
		List<Project> prList=projectService.getAllProjectName();
		List<CheckUnit> cuList=checkUnitService.getAllCheckUnit();
		List<Part>  partList=PartService.getAllPart();
		model.addAttribute("prList", prList);
		model.addAttribute("cuList", cuList);
		model.addAttribute("partList", partList);
		System.out.println("contractList");
		return "priceAndYkl";
	}
    
	@RequestMapping("/selectPriceAndYkl")
	public @ResponseBody Page<Contract> selectPriceAndYkl(@RequestBody Map<String, Object> map,HttpSession session){
		
		
		System.out.println("selectPriceAndYkl");
		Contract contract=new Contract();
		contract.setPrId((map.get("prId").toString()));
	
	
		System.out.println(contract.toString());
		Page<Contract> pagePrice=contractService.selectPriceAndYkl(map);
		return pagePrice;
	}
	
	
	@RequestMapping("/selectPriceAndYkl1")
	public @ResponseBody Map<String, Object> selectPriceAndYkl1(@RequestBody Map<String, Object> map,HttpSession session){
		
		
		
		return contractService.selectPriceAndYkl1(map);
	}

	@RequestMapping("uploadFile")
	public String uploadFile(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam("uploadfile") MultipartFile uploadfile, RedirectAttributes attr, Model model) throws IOException {
		
		
		String filename = uploadfile.getOriginalFilename();
		//设置允许上传文件类型
        String suffixList = ".xlsx,.xls";
        // 获取文件后缀
        String suffix = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

		String path = "D:/cyjz_file/price/" ;
		long gfilesize = 0;
		String gfilename = "";
		
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		// 获取文件名称
		int dot = filename.lastIndexOf('.'); 
		gfilename = filename.substring(0, dot);
		gfilesize = uploadfile.getSize();
		
		
		if (filename == null || filename == "") {
			System.out.println("上传失败!----文件名为空或路径不正确!");
			attr.addFlashAttribute("msg", "上传失败！<br/>文件名为:" + gfilename + "<br/>大小为:"+ gfilesize + "字节" + "<br/>出错信息为:<br/>--- 文件名为空或路径不正确!");
		}
		else if (uploadfile.getSize() > 20 * 1024 * 1024) {
			System.out.println("上传失败!----所传文件过大!");
			attr.addFlashAttribute("msg", "上传失败！<br/>文件名为:" + gfilename + "<br/>大小为:"+ gfilesize + "字节" + "<br/>出错信息为:<br/>--- 所传文件过大!");
		}
		else if (!suffixList.contains(suffix.trim().toLowerCase())) {
			System.out.println("文件类型不合法!只能为xlsx,xls类型的文件");
			attr.addFlashAttribute("msg", "上传失败！<br/>文件名为:"+ gfilename+ "<br/>大小为:"+ gfilesize + "字节" + "<br/>出错信息为:<br/>--- 文件类型不合法!只能为xlsx,xls类型的文件");
		}

		else {
			uploadfile.transferTo(new File(path + File.separator + filename));
			System.out.println("上传成功!<br>文件名为:" + filename + "<br/>大小为:" + gfilesize + "字节");
			attr.addFlashAttribute("msgsuccess", "上传成功!<br>文件名为:" + filename + "<br/>大小为:" + gfilesize + "字节");
		}
		attr.addAttribute("filename", filename);
		return "redirect:/contract/toImportPrice";
	}
	
	@RequestMapping(value="/importExcel",produces="text/html;charset=UTF-8")
	public  String importExcel(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			  Model model) throws IOException {
		String filename=request.getParameter("filename");
		System.out.println("importExcel");
		System.out.println(filename);
		String filepath = "原本是空的";
		filepath = "D:/cyjz_file/price/" + filename;
		System.out.println("filepath=" + filepath);
		// 获取excel 文件
		List<Contract> contracts = new ArrayList<Contract>();
		// 根据杨师兄提供的数据库表来命名
		BigDecimal budgetSum=new BigDecimal(999999.00);
		
		String prId;
		String prName = "";// 工程名称
		String cuName = "";// 班组（结算单位）
		String cuId;
		String pcpId;
		String unitNumber = "";// 栋号		
		String pName = "";// 分部
		String unId;// 单位
		String unName;
		String price;// 单价
		String message = "";// 提示信息未关联
		int sumCount = 0;// 导入总条数
		int existCount = 0;// 数据库已存在的数据的条数
		int saveCount = 0;// 成功导入的条数
		
		List<Part> partList=PartService.getAllPart();
		String partId;
		if (partList.size()>0) {
			int pSize=partList.size()-1;
			String pCode=partList.get(pSize).getPartId();
		    partId=com.org.cygs.util.common.StringUtil.autoIncrement(pCode);
		}else {
			partId="0001";
		}
		
		Job jobNew=JobService.getNewJobKey();
		String jobId;
		if (jobNew!=null) {
			jobId=jobNew.getJobId();
			jobId=com.org.cygs.util.common.StringUtil.autoIncrement(jobId);
		}else {
			jobId="0001";
		}
		
		// 创建对Excel工作簿文件的引用
		HSSFWorkbook hssfworkbook = new HSSFWorkbook(new FileInputStream(
				filepath));
		HSSFSheet hssfsheet;
		HSSFRow[] hssfrow = new HSSFRow[50];// 预计50行 hssfrow里存有了所有的行。
		HSSFRow hssfrow1;
		hssfsheet = hssfworkbook.getSheetAt(0);// 直接取第一个表单.三个表单的第一个。
		
		for (int j = 0; j < hssfsheet.getPhysicalNumberOfRows(); j++) {
			hssfrow[j] = hssfsheet.getRow(j);
			// 判断是否还存在需要导入的数据
			if (null == hssfsheet.getRow(j)) {
				System.out.println("这里已没有数据，在第" + j + "行");
				break;
			}
		}
		// ///////////////////////////////////////////蒙泳行-开始修改//////////////
		// 判断要独立出来
		boolean right = true;
		prName = hssfrow[0].getCell(1).getStringCellValue().trim(); // 工程
		List<Project> prList  = projectService.getProjectByPrName(prName);
		
		if (prList.size() == 0) {
			right = false;
			message += "工程名称(" + prName + ")不存在,请检查;";
			prId=null;
		}else {
			Project project =prList.get(0);
			prId=project.getPrId();	
		}
		
		
		List<CheckUnit> cuList=new ArrayList<CheckUnit>();
		for (int cu = 1; cu < hssfrow[1].getLastCellNum(); cu++) {			
			if (hssfrow[1].getCell(cu) == null) {
				continue;
			} else if (hssfrow[1].getCell(cu).getCellType() == 0) {
				cuName = new Double(hssfrow[1].getCell(cu)
						.getNumericCellValue()).toString().trim();
			} else {// 如果EXCEL表格中的数据类型为字符串型
				cuName = hssfrow[1].getCell(cu).getStringCellValue().trim();
				if (cuName.equals("")) {
					continue;
				}				
			}	
			cuList = checkUnitService.selectCheckUnitListByName(cuName);
			if (cuList.size() == 0) {
				right = false;
				message += "班组（结算单位）(" + cuName + ")不存在,请检查;";
				break;
			}
		}
		
		
		List<PrIndexPC> pcpList=new ArrayList<PrIndexPC>();
		for (int pcp = 1; pcp < hssfrow[2].getLastCellNum(); pcp++)// 第三行取栋号
		{
			if (hssfrow[2].getCell(pcp) == null) {
				continue;
			} else if (hssfrow[2].getCell(pcp).getCellType() == 0) {
				unitNumber = new Double(hssfrow[2].getCell(pcp).getNumericCellValue()).toString().trim();
			} else {// 如果EXCEL表格中的数据类型为字符串型
				unitNumber = hssfrow[2].getCell(pcp).getStringCellValue().trim();
				if (unitNumber.equals("")) {
					continue;
				}				
				 pcpList= projectService.getPrIndexPCByOption(prId, unitNumber);
				if (pcpList.size() == 0) {
					right = false;
					message += "栋号不存在(" + unitNumber + ")不存在,请检查;";
					break;
				}
			} 			
		}
		
		
		// 判断单位是否存在，第五行不取值，从第六行开始
		Unit unit=new Unit();
		for (int s = 5; s < hssfsheet.getPhysicalNumberOfRows(); s++) {
			hssfrow1 = hssfsheet.getRow(s);
			if (hssfrow1.getCell(3) == null) {
				continue;
				} 
			else {// 如果EXCEL表格中的数据类型为字符串型
				unName = hssfrow1.getCell(3).getStringCellValue().trim();// 单位
			if (unName.equals("")) {
				continue;
			}			
			 unit = unitService.getUnitByName(unName);
			if (unit == null) {
				right = false;
				message += "单位不存在(" + unit + ")不存在,请检查;";
				break;
				}
			}
		}
		
		
		//////////////////////////////////判断结束，开始处理///////////////////////////////////////////////////
		if (right) {
			for (int cu = 1; cu < hssfrow[1].getLastCellNum(); cu++) {//取结算单位
				if (hssfrow[1].getCell(cu) == null) {
					continue;
				} else if (hssfrow[1].getCell(cu).getCellType() == 0) {
					cuName = new Double(hssfrow[1].getCell(cu)
							.getNumericCellValue()).toString().trim();
				} else {// 如果EXCEL表格中的数据类型为字符串型
					cuName = hssfrow[1].getCell(cu)
							.getStringCellValue().trim();
					if (cuName.equals("")) {
						continue;
					}
				}
				cuList = checkUnitService.selectCheckUnitListByName(cuName);
				cuId=cuList.get(0).getCuId();
				for (int pcp = 1; pcp < hssfrow[2].getLastCellNum(); pcp++)// 第三行取栋号
				{
					if (hssfrow[2].getCell(pcp) == null) {
						continue;
					} else if (hssfrow[2].getCell(pcp).getCellType() == 0) {
						unitNumber = new Double(hssfrow[2].getCell(pcp).getNumericCellValue()).toString().trim();
					} else {// 如果EXCEL表格中的数据类型为字符串型
						unitNumber = hssfrow[2].getCell(pcp)
								.getStringCellValue().trim();
						if (unitNumber.equals("")) {
							continue;
						}
					}
					pcpList= projectService.getPrIndexPCByOption(prId, unitNumber);
					PrIndexPC prpc=pcpList.get(0);
				    pcpId=prpc.getPcPId();
					for (int p = 1; p < hssfrow[3].getLastCellNum(); p++)// 第四行取分部
					{
						
						 if (hssfrow[3].getCell(p) == null) {
							continue;
						} else if (hssfrow[3].getCell(p)
								.getCellType() == 0) {
							pName = new Double(hssfrow[3].getCell(p).getNumericCellValue())
									.toString().trim();
						} else {// 如果EXCEL表格中的数据类型为字符串型
							pName = hssfrow[3].getCell(p)
									.getStringCellValue().trim();
							if (pName.equals("")) {
								continue;
							}
						} 
						// P_ID varchar 32 分部主键
						// P_CODE char 4 分部编号
						// P_NAME varchar 20 分部名称
						// ISOLDNEW int 4 新旧合同类别(范围为:1:新/0:旧)
						// PR_ID varchar 32 工程主键(旧合同为空,新必须加ID)
						// PC_P_ID varchar 32 工程类别，栋号相关表主键(旧合同为空,新必须加ID)
						//2011-03-07 加CU_ID

						// 如果该 工程，栋号 ,结算单位，下该 分部 已存在，则取出，否则新建一个分部
					
						Part part=new Part();
						
						part.setpName(pName);
						
						List<Part> pList=PartService.getPartListByPart(part);
						
						if (pList.size() == 0) { // 该分部未存在，新建分部				
							part.setPartId(partId);							
						    System.out.println(part.toString());
							PartService.addPart(part);
							partId=StringUtil.autoIncrement(partId);
							List<Part> newList=PartService.getPartListByPart(part);
							part = newList.get(0);
						} else { // 该分部已存在，取出
							part = pList.get(0);
						}
						// 到此分部已确定为某一个
						// 下面开始分项1
						// 第五行不取值，从第六行开始
						for (int k = 5; k < hssfsheet.getPhysicalNumberOfRows(); k++) {
							hssfrow1 = hssfsheet.getRow(k);
							String fenx1 ="";
							if(hssfrow1.getCell(0)==null){
								continue;
							}else{
							fenx1 = hssfrow1.getCell( 0)
									.getStringCellValue().trim();// 分项1
							// 如果分项1为空，略过这行
							}
							if (fenx1.equals("")) {
								continue;
							}
							String fenx2 ="";
							
							if(hssfrow1.getCell(1)!=null){							
									if (hssfrow1.getCell(1).getCellType() == 0) {
										fenx2 = new Double(hssfrow1.getCell(1).getNumericCellValue()).toString().trim();
									} else {// 如果EXCEL表格中的数据类型为字符串型
										fenx2 = hssfrow1.getCell(1)
												.getStringCellValue().trim();					
										}	
								//fenx2 = hssfrow1.getCell(1).getStringCellValue().trim();// 分项2
							}
							
																					
							// 如果分项2为空，默认等于分项1,分项3也设为分项1，不管此时分项3的值
							if (fenx2.equals("")) {
								fenx2 = fenx1;							
							}
							// 如果分项3为空，则默认为分项2
							
							// 判断该分部下，该分项1 是否已存在，存在则取出，否则新建一个分项1
							
							// 分项1 工程部位
							// 表名 PART_POSITION 备注 旧合同工程部位基础信息+新合同的分项1信息.
							// 中文表名 工程部位                  				
							// 中文字段名 字段名 字段类型 长度 备注(约束条件,或数字意义)
							// PS_ID varchar 32 工程部位主键
							// P_ID varchar 32 分部主键
							// PS_NAME varchar 50 工程部位名称
							// REMARK varchar 500 备注

							PartPosition ps=new PartPosition();
							ps.setpId(part.getpId());
							ps.setPsName(fenx1);
							List<PartPosition> psList=PartService.getPartPositionList1(ps);
							
							if (psList.size() == 0) { // 该分项1没有重复，新建一个分项1
								
								ps.setRemark("分项1");
								System.out.println(ps.toString());
								PartService.addPartPosition(ps);
								List<PartPosition> newList=PartService.getPartPositionList1(ps);
								ps = newList.get(0);
							} else { // 该分项1已经存在，取出
								ps = psList.get(0);
							}

							// 分项2 工作项目
							
							// 表名 OPERATION 备注 旧合同中的工作项目表,新合同的分项2的基础信息.
							// 中文表名 工作项目              				
							// 中文字段名 字段名 字段类型 长度 备注(约束条件,或数字意义)
							// O_ID varchar 32 工作项目主键
							// O_CODE char 4 工作项目编号(可以不用这个编号?)
							// O_NAME varchar 100 工作项目名称(分项2的名称)
							// REMARK varchar 4000 备注(工作内容信息)
							// PS_ID varchar 32 工程部位主键(分项1的主键)
							// UN_ID varchar 32 单位主键
							
							unName = hssfrow1.getCell(3).getStringCellValue().trim();// 单位
							unit = unitService.getUnitByName(unName);
							unId=unit.getUnId();
							String workcontents = "";
							
						
							Job job=new Job();
							job.setPsId(ps.getPsId());
							job.setJobName(fenx2);
							job.setUnId(unId);
							
                            List<Job> jobList=JobService.selectJobs(job);
                            		
							if (jobList.size() == 0) { // 该分项2没有重复，新建一个分项2
								for (int n = 5; n < hssfrow1.getLastCellNum(); n++) // 工作内容信息									
									if (hssfrow1.getCell(n)!=null) {
										if (hssfrow1.getCell(n).getCellType() == 0) {
											workcontents += new Double(hssfrow1.getCell(n)
													.getNumericCellValue()).toString().trim();
										} else {// 如果EXCEL表格中的数据类型为字符串型
											workcontents += hssfrow1.getCell(n)
													.getStringCellValue().trim();					
											}							
									}
								
								System.out.println("workcontents:"+workcontents);
								
								job.setJobId(jobId);								
								job.setJobName(fenx2);								
								job.setRemark(workcontents);
								System.out.println(job.toString());
								JobService.addJob(job);
								jobId=StringUtil.autoIncrement(jobId);
								List<Job> newList=JobService.selectJobs(job);
								job = newList.get(0);
							} else { // 该分项2已经存在，取出
								job = jobList.get(0);
							}

						
							// 表名 OPERATION_PRICE 备注 土建单价信息表(新的单价信息表,新旧合同统一为该表)
							// 中文表名 单价            				
							// 中文字段名 字段名 字段类型 长度 备注(约束条件,或数字意义)
							// OP_ID varchar 32 单价主键
							// O_ID varchar 32 工作项目主键
							// UN_ID varchar 32 单位主键
							// PRICE float 8 价格
							// PR_ID varchar 32 工程名称主键
							// PS_ID varchar 32 工程部位主键
							// PC_P_ID varchar 32 与工程类别，栋号相关表主键
							// CU_ID varchar 32 结算单位主键
							// FS_ID varchar 32 分项3/工作项目重复ID
							// B_ID varchar 32 预算基础信息ID
							// ISBUDGET int 4
							// 新/旧合同。0-旧合同。1-新合同。(改预算为新旧标志,11-2-26)
							//考虑重复导入的单价信息
							//2011-03-05
							
						
							Contract contract=new Contract();
							contract.setPcpId(pcpId);
							contract.setPrId(prId);
							contract.setCuId(cuId);	
							contract.setPrName(prName);
							contract.setCuName(cuName);
							contract.setUnName(unName);
							contract.setPcpNumber(unitNumber);							
							contract.setpName(part.getpName());
							contract.setPsName(ps.getPsName());							
							contract.setJobName(job.getJobName());
							
							List<Contract> cList=contractService.selectContracts(contract);
							System.out.println(cList.size());
							if (cList.size() == 0) { // 该单价没有重复，新建一个单价
								
								price = new Float(hssfrow1.getCell(4).getNumericCellValue()).toString().trim(); 
								Float fp =new Float(price.toString());								
								contract.setpId(part.getpId());
								contract.setPsId(ps.getPsId());
								contract.setJobKey(job.getJobKey());
								contract.setUnId(unId);															
								contract.setPrice(fp);
								contract.setIsbudget(1);//新合同单价
								contract.setBudgetSum(budgetSum);
								contract.setRemark(workcontents);
								System.out.println(contract.toString());
								contractService.addContract(contract);
								saveCount++;
							}else{// 该单价有重复，赋新的price值
								contract = cList.get(0);
								price = new Float(hssfrow1.getCell(4).getNumericCellValue()).toString().trim(); 
								Float fp =new Float(price.toString());
								contract.setPrice(fp);
								contractService.updateContract(contract);
								existCount++;
							}
							
							
							
							
//							private String isbudget;//类别（新旧）
//							private String prName;//工程名称
//							private String unitNumber;//栋号
//							private String cuName;//班组（结算单位）
//							private String part;//分部
//							private String fenx1;//分项1
//							private String fenx2;//分项2
//							private String fenx3;//分项3
//							private String unId;//单位
//							private Float price;//单价
//							private String workContent;//工作内容
							unName = hssfrow1.getCell(3).getStringCellValue().trim();// 单位
							
							contracts.add(contract);
						}
					}
				}
			}
		}
		// ////////////////////////////////////////////////////////////////////
        System.out.println(message+"I am here");
		model.addAttribute("contracts", contracts);
		model.addAttribute("message", message);
		model.addAttribute("sumCount", sumCount);
		model.addAttribute("existCount", existCount);
		model.addAttribute("saveCount", saveCount);
		System.out.println("出错了吗？");
		return "importedExcel";		
	}
	
	// 下载附件
		@RequestMapping("/downloadFile")
		public void downloadFile(HttpServletRequest request, HttpSession session, HttpServletResponse response, RedirectAttributes attr, Model model) throws IOException {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				String filepath = "D:/cyjz_file/price/model/template.xls";			
				response.setContentType("application/x-msdownload");
				response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("template.xls", "utf-8"));
				bis = new BufferedInputStream(new FileInputStream(new File(filepath)));
				bos = new BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[20480]; // 缓冲20K
				int bytesRead;
				while ((bytesRead = bis.read(buff, 0, buff.length)) != -1) {
					bos.write(buff, 0, bytesRead);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			}
		}
		
}
