package com.zxl.interceptorlimit.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Describe
 * @Author zxl
 * @Date 2019-04-28 15:01
 */
public class URLInterceptor implements HandlerInterceptor {

    //  @Autowired
    //  private BlacklistDao blacklistDao;
    private Map<String, Integer> redisTemplate = new HashMap<String, Integer>();
    private static final Logger logger = LoggerFactory.getLogger("RequestLimitLogger");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        String ip = request.getLocalAddr();
        // List<Blacklist> blackList =blacklistDao.findByIp(ip);
        //  if (blackList == null || blackList.size() == 0) {
        //10秒内不能超过10次
        urlHandle(request, 10000, 10);
       /* } else {
            modelAndView.setViewName("/errorpage/error");
        }*/
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

    public void urlHandle(HttpServletRequest request, long limitTime, int limitCount) throws RequestLimitException {
        try {
            String ip = request.getLocalAddr();
            String url = request.getRequestURL().toString();
            String key = "req_limit_".concat(url).concat(ip);
            if (redisTemplate.get(key) == null || redisTemplate.get(key) == 0) {
                redisTemplate.put(key, 1);
            } else {
                redisTemplate.put(key, redisTemplate.get(key) + 1);
            }
            int count = redisTemplate.get(key);
            if (count > 0) {
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        redisTemplate.remove(key);
                    }
                };
                timer.schedule(task, limitTime);
            }
            if (count > limitCount) {
                // addHostHandle(ip);
                throw new RequestLimitException();
            }
        } catch (RequestLimitException e) {
            throw e;
        } catch (Exception e) {
            logger.error("发生异常: ", e);
        }
    }

    public void addHostHandle(String ip) {
        // Calendar calendar = Calendar.getInstance();
        //  Date iptime = calendar.getTime();
        //   Blacklist blacklist=new Blacklist(ip,iptime);
        //    blacklistDao.save(blacklist);
    }

}
