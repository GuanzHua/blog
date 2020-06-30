package com.guanzh.web.admin;

import com.guanzh.po.Tag;
import com.guanzh.po.Type;
import com.guanzh.service.TagService;
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
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/tags")
    public String tags(@PageableDefault(size = 5, sort = {"id"}) Pageable pageable, Model model) {

        Page<Tag> tags = tagService.listTag(pageable);
        model.addAttribute("page",tags);

        return "admin/tags";
    }

    //到新增标签页面
    @GetMapping("/tags/input")
    public String input(Model model) {
        model.addAttribute("tag",new Tag());
        return "admin/tags-input";
    }

    //到修改页面
    @GetMapping("/tags/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        Tag tag = tagService.getTag(id);
        model.addAttribute("tag", tag);
        return "admin/tags-input";
    }

    //保存分类
    @PostMapping("/tags")
    public String post(@Valid Tag tag, BindingResult result, RedirectAttributes attributes) {

        //根据分类名称查询，如果已存在分类，返回提示
        Tag tagByName = tagService.getTagByName(tag.getName());
        if(tagByName != null) {
            result.rejectValue("name","nameErroe", "标签名称已存在，请重新输入！");
        }

        //校验分类名称
        if(result.hasErrors()) {
            return "admin/tags-input";
        }

        //保存
        Tag saveTag = tagService.saveTag(tag);
        if(saveTag == null) {
            //保存失败
            attributes.addFlashAttribute("message", "标签添加失败！");
        }else {
            //保存成功
            attributes.addFlashAttribute("message", "标签添加成功！");
        }
        return "redirect:/admin/tags";
    }

    //更新分类
    @PostMapping("/tags/{id}")
    public String editPost(@Valid Tag tag, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {

        //根据分类名称查询，如果已存在分类，返回提示
        Tag tagByName = tagService.getTagByName(tag.getName());
        if(tagByName != null) {
            result.rejectValue("name","nameErroe", "标签名称已存在，请重新输入！");
        }

        //校验分类名称
        if(result.hasErrors()) {
            return "admin/tags-input";
        }

        //保存
        Tag saveTag = tagService.updateTag(id, tag);
        if(saveTag == null) {
            //保存失败
            attributes.addFlashAttribute("message", "标签更新失败！");
        }else {
            //保存成功
            attributes.addFlashAttribute("message", "标签更新成功！");
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/tags/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        tagService.deleteTag(id);
        attributes.addFlashAttribute("message", "标签删除成功！");
        return "redirect:/admin/tags";
    }
}
