package com.wt.mis.sms.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "smsserver_out")
public class SmsserverOut implements Serializable {

    /**
     * 设置自增字段
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String recipient;
    private String text;

    @Column(name = "wap_url")
    private String wapUrl;

    @Column(name = "wap_expiry_date")
    private Date wapExpiryDate;

    @Column(name = "wap_signal")
    private String wapSignal;

    @Column(name = "create_date")
    private Date createDate;

    private String originator;
    private String encoding;

    @Column(name = "status_report")
    private Integer statusReport;

    @Column(name = "flash_sms")
    private Integer flashSms;

    @Column(name = "src_port")
    private Integer srcPort;

    @Column(name = "dst_port")
    private Integer dstPort;

    @Column(name = "sent_date")
    private Date sentDate;

    @Column(name = "ref_no")
    private String refNo;

    private Integer priority;
    private String status;
    private Integer errors;

    @Column(name = "gateway_id")
    private String gatewayId;

    @Column(name = "user_id")
    private String userId;
}
