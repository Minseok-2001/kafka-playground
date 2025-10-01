import http from "k6/http";
import {check, sleep} from "k6";
import {Counter} from "k6/metrics";

export const options = {
  scenarios: {
    steady_load: {
      executor: "constant-arrival-rate",
      rate: Number(__ENV.K6_RATE || 20),
      timeUnit: "1s",
      duration: __ENV.K6_DURATION || "2m",
      preAllocatedVUs: Number(__ENV.K6_VUS || 50),
      maxVUs: Number(__ENV.K6_VUS_MAX || 100),
    },
  },
  thresholds: {
    http_req_duration: ["p(95)<500"],
    http_req_failed: ["rate<0.01"],
    waiting_issue_success: ["count>0"],
  },
};

const BASE_URL = __ENV.BASE_URL || "http://waiting-service:8087";
const QUEUE_CODE = __ENV.QUEUE_CODE || "concert";
const MEMBER_OFFSET = Number(__ENV.MEMBER_OFFSET || 100000);

const issueSuccess = new Counter("waiting_issue_success");

export default function () {
  const memberId = MEMBER_OFFSET + __VU * 100000 + __ITER;
  const issueRes = http.post(
      `${BASE_URL}/api/waiting/${QUEUE_CODE}/tickets`,
      JSON.stringify({memberId}),
      {
        headers: {
          "Content-Type": "application/json",
        },
        tags: {name: "issue-ticket"},
      },
  );

  const okIssue = check(issueRes, {
    "issue status 201": (r) => r.status === 201,
    "issue returns ticket id": (r) => !!r.json("id"),
  });

  if (!okIssue) {
    return;
  }

  issueSuccess.add(1);
  const ticketId = issueRes.json("id");

  const statusRes = http.get(
      `${BASE_URL}/api/waiting/${QUEUE_CODE}/tickets/${memberId}`,
      {tags: {name: "get-status"}},
  );

  check(statusRes, {
    "status 200": (r) => r.status === 200,
    "status matches ticket": (r) => Number(r.json("ticketId")) === Number(
        ticketId),
    "status waiting": (r) => r.json("status") === "WAITING",
  });

  sleep(Number(__ENV.K6_SLEEP || 1));
}
