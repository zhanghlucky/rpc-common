package com.hui.zhang.common.util.performance.interceptor;

import com.hui.zhang.common.util.performance.model.MethodExecuteInfo;
import com.hui.zhang.common.util.performance.model.StopWatch;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 * 拦截器
 */
public class PerformanceInterceptor extends HandlerInterceptorAdapter {

	private static StopWatch stopWatch = new StopWatch();

	private static final String PERFORMANCE_INFO = "performanceInfo";

	@Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response, Object handler) throws Exception {

		PerformanceTask performanceTask = new PerformanceTask();
		MethodExecuteInfo methodExecuteInfo = stopWatch.start(performanceTask);
		methodExecuteInfo.setMethodName(request.getRequestURI());
		request.setAttribute(PERFORMANCE_INFO, methodExecuteInfo);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
						   HttpServletResponse response, Object handler,
						   ModelAndView modelAndView) throws Exception {

		Object o = request.getAttribute(PERFORMANCE_INFO);


		if (o != null) {
			MethodExecuteInfo methodExecuteInfo = (MethodExecuteInfo) o;
			PerformanceTask task = (PerformanceTask)methodExecuteInfo.getTask();
			task.setType(generateOperatonIdendifier(request));
			stopWatch.stop(methodExecuteInfo);
		}
	}

	/**
	 * 生成task type
	 * @param request
	 * @return
	 */
	private String generateOperatonIdendifier(HttpServletRequest request) {
		String uri = request.getRequestURI();
		StringBuilder sb = new StringBuilder(64);
		// 方法
		String method = request.getMethod();
		sb.append(method).append("|").append(uri);
		return sb.toString();
	}

}
