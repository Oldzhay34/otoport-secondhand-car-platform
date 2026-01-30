package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.InquiryMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface InquiryMessageRepository extends JpaRepository<InquiryMessage, Long> {


    long countByInquiryIdAndReadByClientFalse(Long inquiryId);

    // ✅ arşivlenecek inquiry id listesi
    @Query("""
       select distinct m.inquiry.id
       from InquiryMessage m
       where m.sentAt < :before
    """)
    List<Long> findInquiryIdsHavingMessagesBefore(@Param("before") Instant before);

    // ✅ bir inquiry için arşivlenecek mesajlar (old)
    @Query("""
       select m
       from InquiryMessage m
       where m.inquiry.id = :inquiryId and m.sentAt < :before
       order by m.sentAt asc
    """)
    List<InquiryMessage> findOldMessagesForInquiry(@Param("inquiryId") Long inquiryId,
                                                   @Param("before") Instant before);

    // ✅ old mesajları sil (chunk yazıldıktan sonra)
    @Modifying
    @Query("""
        delete from InquiryMessage m
        where m.inquiry.id = :inquiryId and m.sentAt < :before
    """)
    int deleteOldMessagesForInquiry(@Param("inquiryId") Long inquiryId,
                                    @Param("before") Instant before);

    @Query("""
      select m.inquiry.store.id, count(m)
      from InquiryMessage m
      where m.sentAt >= :start and m.sentAt < :end
      group by m.inquiry.store.id
    """)
    List<Object[]> countMessagesByStore(Instant start, Instant end);

    @Query("""
      select m.inquiry.store.id, count(m)
      from InquiryMessage m
      where m.readByStore = false
      group by m.inquiry.store.id
    """)
    List<Object[]> countUnreadByStore();

    @Query("""
      select function('hour', m.sentAt), count(m)
      from InquiryMessage m
      where m.sentAt >= :start and m.sentAt < :end
      group by function('hour', m.sentAt)
      order by function('hour', m.sentAt)
    """)
    List<Object[]> countHourly(Instant start, Instant end);


    @Query("""
        select m
        from InquiryMessage m
        where m.inquiry.id = :inquiryId
        order by m.sentAt asc
    """)
    List<InquiryMessage> findAllByInquiryIdOrderBySentAt(@Param("inquiryId") Long inquiryId);

    @Query("""
        select m.inquiry.store.id, count(m)
        from InquiryMessage m
        where m.sentAt >= :start and m.sentAt < :end
        group by m.inquiry.store.id
    """)
    List<Object[]> countByStoreBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("""
        select m
        from InquiryMessage m
        where m.inquiry.id = :inquiryId
        order by m.sentAt asc
    """)
    List<InquiryMessage> findByInquiryIdOrderBySentAtAsc(@Param("inquiryId") Long inquiryId);

    @Query("""
        select count(m)
        from InquiryMessage m
        where m.inquiry.id = :inquiryId
          and m.senderType = 'CLIENT'
          and m.readByStore = false
    """)
    long countUnreadForStore(@Param("inquiryId") Long inquiryId);

    // controller’da kullandığın isim buydu → aynı bırakıyorum
    @Query("""
        select count(m)
        from InquiryMessage m
        where m.inquiry.id = :inquiryId
          and m.readByStore = false
    """)
    long countByInquiryIdAndReadByStoreFalse(@Param("inquiryId") Long inquiryId);

    @Modifying
    @Query("""
        update InquiryMessage m
        set m.readByStore = true
        where m.inquiry.id = :inquiryId
          and m.senderType = 'CLIENT'
          and m.readByStore = false
    """)
    int markReadByStore(@Param("inquiryId") Long inquiryId);


}
