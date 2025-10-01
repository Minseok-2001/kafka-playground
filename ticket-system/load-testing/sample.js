import http from "k6/http";
import {check, sleep} from "k6";

export const options = {
  vus: Number(__ENV.K6_VUS || 20),
  duration: __ENV.K6_DURATION || "1m",
  thresholds: {
    http_req_duration: ["p(95)<500"],
    http_req_failed: ["rate<0.01"],
  },
};

export default function () {
  const target = __ENV.TARGET_URL
      || "http://host.docker.internal:8080/actuator/health";
  const res = http.get(target);
  check(res, {
    "status is 200": (r) => r.status === 200,
  });
  sleep(Number(__ENV.K6_SLEEP || 1));
}
