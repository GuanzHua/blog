package com.guanzh.web.admin;

import com.guanzh.po.Type;
import com.guanzh.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("/types")
    public String types(@PageableDefault(size = 5, sort = {"id"}) Pageable pageable, Model model) {

        Page<Type> types = typeService.listType(pageable);
        model.addAttribute("page",types);

        return "admin/types";
    }

    //到新增分类页面
    @GetMapping("/types/input")
    public String input(Model model) {
        model.addAttribute("type",new Type());
        return "admin/types-input";
    }

    //到修改页面
    @GetMapping("/types/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        Type type = typeService.getType(id);
        model.addAttribute("type", type);
        return "admin/types-input";
    }

    //保存分类
    @PostMapping("/types")
    public String post(@Valid Type type, BindingResult result, RedirectAttributes attributes) {

        //根据分类名称查询，如果已存在分类，返回提示
        Type typeByName = typeService.getTypeByName(type.getName());
        if(typeByName != null) {
            result.rejectValue("name","nameErroe", "分类名称已存在，请重新输入！");
        }

        //校验分类名称
        if(result.hasErrors()) {
            return "admin/types-input";
        }

        //保存
        Type saveType = typeService.saveType(type);
        if(saveType == null) {
            //保存失败
            attributes.addFlashAttribute("message", "分类添加失败！");
        }else {
            //保存成功
            attributes.addFlashAttribute("message", "分类添加成功！");
        }
        return "redirect:/admin/types";
    }

    //更新分类
    @PostMapping("/types/{id}")
    public String editPost(@Valid Type type, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {

        //根据分类名称查询，如果已存在分类，返回提示
        Type typeByName = typeService.getTypeByName(type.getName());
        if(typeByName != null) {
            result.rejectValue("name","nameErroe", "分类名称已存在，请重新输入！");
        }

        //校验分类名称
        if(result.hasErrors()) {
            return "admin/types-input";
        }

        //保存
        Type saveType = typeService.updateType(id, type);
        if(saveType == null) {
            //保存失败
            attributes.addFlashAttribute("message", "分类更新失败！");
        }else {
            //保存成功
            attributes.addFlashAttribute("message", "分类更新成功！");
        }
        return "redirect:/admin/types";
    }

    @GetMapping("/types/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        typeService.deleteType(id);
        attributes.addFlashAttribute("message", "分类删除成功！");
        return "redirect:/admin/types";
    }


}
