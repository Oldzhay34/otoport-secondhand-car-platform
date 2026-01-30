package com.example.otoportdeneme.dto_Requests;

public class WalSearchRequest {
    private Integer limit; // 1..500
    private String sort;   // "asc" | "desc"

    private String actorType; // "ADMIN"
    private Long actorId;

    private String method; // GET/POST...
    private Integer status;

    private String pathContains;
    private String q; // request/response body içinde arama (küçük projelerde yeterli)

    // ISO-8601 Instant string: "2026-01-27T00:00:00Z"
    private String from;
    private String to;

    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }

    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }

    public String getActorType() { return actorType; }
    public void setActorType(String actorType) { this.actorType = actorType; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getPathContains() { return pathContains; }
    public void setPathContains(String pathContains) { this.pathContains = pathContains; }

    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
}
