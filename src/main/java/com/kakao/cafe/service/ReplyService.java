package com.kakao.cafe.service;

import com.kakao.cafe.domain.Reply;
import com.kakao.cafe.dto.reply.ReplyDto;
import com.kakao.cafe.repository.reply.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository repository;
    private final ModelMapper modelMapper;

    public long create(long userId, long articleId, String comments) {
        Reply reply = new Reply(userId, articleId, comments, LocalDateTime.now());
        reply = repository.save(reply);
        return reply.getId();
    }

    public long delete(long id) {
        return repository.delete(id);
    }

    public long deleteByArticleId(long id) {
        return repository.deleteByArticleId(id);
    }

    public List<ReplyDto> findAllReplyByArticleId(long id) {
        return repository.findAllReply(id).stream()
                .map(m -> modelMapper.map(m, ReplyDto.class))
                .map(this::setUserNickname)
                .collect(Collectors.toList());
    }

    private ReplyDto setUserNickname(ReplyDto dto) {
        String nickname = repository.findUserNicknameById(dto.getId()).
                orElse("탈퇴한 사용자");
        dto.setNickname(nickname);
        return dto;
    }
}
