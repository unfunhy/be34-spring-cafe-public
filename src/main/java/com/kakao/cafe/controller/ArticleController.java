package com.kakao.cafe.controller;

import com.kakao.cafe.dto.article.ArticleCreationDto;
import com.kakao.cafe.service.ArticleService;
import com.kakao.cafe.service.ReplyService;
import com.kakao.cafe.util.SessionIdRequired;
import com.kakao.cafe.util.Url;
import com.kakao.cafe.util.View;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ReplyService replyService;

    @GetMapping("/articles")
    public String getAllArticle(Model model) {
        var articleList = articleService.getAllArticles();
        model.addAttribute("articleList", articleList);
        model.addAttribute("articleListSize", articleList.size());
        return View.INDEX;
    }

    @SessionIdRequired
    @GetMapping("/articles/form")
    public String getArticleForm() {
        return View.ARTICLES_FORM;
    }

    @SessionIdRequired
    @PostMapping("/articles")
    public String createArticle(ArticleCreationDto dto, HttpSession session) {
        long userId = (long) session.getAttribute("sessionedUserId");
        dto.setUserId(userId);
        long id = articleService.post(dto);
        return "redirect:/articles/" + id;
    }

    @SessionIdRequired
    @GetMapping("/articles/{id}")
    public String getArticlePage(@PathVariable long id, HttpSession session, Model model) {
        var article = articleService.getById(id);
        var replyList = replyService.findAllReplyByArticleId(id);
        model.addAttribute("article", article);
        model.addAttribute("canEdit", canEdit(session, article.getUserId()));
        model.addAttribute("replyList", replyList);
        model.addAttribute("replyListSize", replyList.size());
        return View.ARTICLES_SHOW;
    }

    @SessionIdRequired
    @PutMapping("/articles/{id}")
    public String updateArticle(@PathVariable long id, HttpSession session, ArticleCreationDto dto) throws Exception {
        long uid = (long) session.getAttribute("sessionedUserId");
        articleService.update(id, uid, dto);
        return "redirect:/articles/" + id;
    }

    @SessionIdRequired
    @GetMapping("/articles/form/{id}")
    public String getUpdateForm(@PathVariable long id, RedirectAttributes attr) {
        var article = articleService.getById(id);
        attr.addFlashAttribute("article", article);
        return "redirect:" + Url.ARTICLES_FORM;
    }

    @SessionIdRequired
    @DeleteMapping("/articles/{id}")
    public String deleteArticle(@PathVariable long id, HttpSession session) throws Exception {
        long sessionUid = (long) session.getAttribute("sessionedUserId");
        articleService.delete(id, sessionUid);
        replyService.deleteByArticleId(id);
        return "redirect:" + Url.ARTICLES;
    }

    @SessionIdRequired
    @PostMapping("/articles/{articleId}/replies")
    public String postReply(@PathVariable long articleId, String comments, HttpSession session) {
        long sessionUid = (long) session.getAttribute("sessionedUserId");
        replyService.create(sessionUid, articleId, comments);
        return "redirect:/articles/" + articleId;
    }

    @SessionIdRequired
    @DeleteMapping("/articles/{articleId}/replies/{id}")
    public String deleteReply(@PathVariable long articleId, @PathVariable long id, HttpSession session) {
        replyService.delete(id);
        return "redirect:/articles/" +articleId;
    }

    private boolean canEdit(HttpSession session, long userId) {
        long uid = (long) session.getAttribute("sessionedUserId");
        return uid == userId;
    }
}
