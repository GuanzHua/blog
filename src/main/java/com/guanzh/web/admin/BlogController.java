package com.guanzh.web.admin;

import com.guanzh.po.Blog;
import com.guanzh.po.Tag;
import com.guanzh.po.Type;
import com.guanzh.po.User;
import com.guanzh.service.BlogService;
import com.guanzh.service.TagService;
import com.guanzh.service.TypeService;
import com.guanzh.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class BlogController {

    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 10, sort = {"createTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model) {
        //初始化分类
        List<Type> types = typeService.listType();
        model.addAttribute("types",types);

        Page<Blog> blogs = blogService.listBlog(pageable, blog);
        model.addAttribute("page",blogs);
        return LIST;
    }

    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 3, sort = {"createTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model) {
        Page<Blog> blogs = blogService.listBlog(pageable, blog);
        model.addAttribute("page",blogs);
        return "admin/blogs :: blogList";
    }

    //到新增页面
    @GetMapping("/blogs/input")
    public String input (Model model) {

        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());
        return INPUT;
    }

    private void setTypeAndTag(Model model) {
        //初始化分类
        List<Type> types = typeService.listType();
        model.addAttribute("types",types);
        //初始化标签
        List<Tag> tags = tagService.listTag();
        model.addAttribute("tags", tags);
    }

    //到修改页面
    @GetMapping("/blogs/{id}/input")
    public String editInput (@PathVariable Long id, Model model) {

        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        blog.init();
        model.addAttribute("blog", blog);
        return INPUT;
    }

    //新增博客或修改博客
    @PostMapping("/blogs")
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session) {

        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));


        //保存博客
        Blog saveBlog;
        if (blog.getId() == null) {
            saveBlog = blogService.saveBlog(blog);
        } else {
            saveBlog = blogService.updateBlog(blog.getId(),blog);
        }

        if(saveBlog == null) {
            //保存失败
            attributes.addFlashAttribute("message", "博客添加失败！");
        }else {
            //保存成功
            attributes.addFlashAttribute("message", "博客添加成功！");
        }
        return REDIRECT_LIST;
    }

    //删除博客
    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "博客删除成功！");
        return REDIRECT_LIST;
    }

}
