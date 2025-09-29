package com.example.newsbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.newsbackend.model.Bookmark;
import com.example.newsbackend.model.User;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUserAndArticleUrl(User user, String articleUrl);
    List<Bookmark> findByUser(User user);
    void deleteByUserAndArticleUrl(User user, String articleUrl);
}
