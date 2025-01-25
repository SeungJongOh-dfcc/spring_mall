package com.sjoh.shop.service;

import com.sjoh.shop.model.Notice;
import com.sjoh.shop.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<Notice> getNoticeAll() {
        return noticeRepository.findAll();
    }

    public Notice getNotice(Long id) throws Exception {
        Optional<Notice> item = noticeRepository.findById(id);
        if(item.isPresent()) {
            return item.get();
        } else {
            throw new Exception("sad");
        }
    }

    public void insertNotice(Notice notice) {
        noticeRepository.save(notice);
    }

    public void updateNotice(Notice notice) {
        noticeRepository.save(notice);
    }

    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }
}
