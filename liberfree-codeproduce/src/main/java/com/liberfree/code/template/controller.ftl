package ${controller};

import com.cupsc.dz.portal.biz.model.PageResponse;
import com.cupsc.dz.portal.biz.model.Pagination;
import com.cupsc.dz.portal.biz.service.BoaBatchRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(ResourceConstants.ROOT_PATH + "/${name}")
public class ${name}Controller extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(${name}Controller.class);


    @Autowired
    ${name}Service service;

    @GetMapping("/list")
    public PageResponse<${name}> list(
            @RequestParam(value = "page",required = false,defaultValue = Pagination.DEFAULT_PAGE + "") Integer page,
            @RequestParam(value = "pageSize",required = false,defaultValue = Pagination.DEFAULT_PAGESIZE + "") Integer pageSize ){
            LOGGER.debug(" list");
        return  service.list(new Pagination(page,pageSize));
    }

    @GetMapping("/{id}")
    public ${name} findById( @PathVariable("id") ${idType} id ){
        return  service.findById(id);
    }




}
