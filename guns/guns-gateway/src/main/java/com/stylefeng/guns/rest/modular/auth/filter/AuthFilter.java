package com.stylefeng.guns.rest.modular.auth.filter;

import com.stylefeng.guns.core.base.tips.ErrorTip;
import com.stylefeng.guns.core.util.RenderUtil;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.user.vo.UserInfoVo;
import io.jsonwebtoken.JwtException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 对客户端请求的jwt token验证过滤器
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:04
 */
public class AuthFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String ignorePath = jwtProperties.getIgnorePath();
        //获得会被放行的一系列路径
        String[] ignores = ignorePath.split(",");
        //获得请求的path
        String path = request.getServletPath();

        //获得请求头的Authorization字段，为了接下来获得请求头的token信息
        //无论请求是否会被filter拦截，在token存在且未过期的情况下都要更新token和用户信息的缓存时间
        final String requestHeader = request.getHeader(jwtProperties.getHeader());
        String authToken = null;

        for (String  ignore: ignores) {
            if(path.contains(ignore)){
                //请求的path是会被filter放行的路径是，不拦截
                //查看被放行的请求是否带有token，如果有且未过期，则刷新时间
                if (requestHeader != null && requestHeader.startsWith("Bearer ")){
                    //获得token
                    authToken = requestHeader.substring(7);
                    Object o = redisTemplate.opsForValue().get(authToken);
                    if(o != null){
                        //请求头携带的token未过期，刷新token和用户信息的缓存时间
                        redisTemplate.expire(authToken,5*60, TimeUnit.SECONDS);
                    }
                }
                chain.doFilter(request, response);
                return;
            }
        }

        //对于被拦截的操作，要进行token验证
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            //请求头带上了token,获取token
            authToken = requestHeader.substring(7);
            //验证token是否过期,以redis里面token是否过期为准（不以请求头中token为准）,包含了验证jwt是否正确
            try {
                Object o = redisTemplate.opsForValue().get(authToken);
                if(o != null){
                    //redis中token对应的用户信息(用户id)存在,说明token未过期
                    //刷新token和用户信息的缓存时间
                    redisTemplate.expire(authToken,5*60, TimeUnit.SECONDS);
                }else{
                    //token不存在或者已经过期
                    //跳转到登录页面
                    return;
                }
            } catch (JwtException e) {
                //有异常就是token解析失败
                RenderUtil.renderJson(response, new ErrorTip(BizExceptionEnum.TOKEN_ERROR.getCode(), BizExceptionEnum.TOKEN_ERROR.getMessage()));
                return;
            }
        } else {
            //header没有带Bearer字段 请求头没有带上token
            RenderUtil.renderJson(response, new ErrorTip(BizExceptionEnum.TOKEN_ERROR.getCode(), BizExceptionEnum.TOKEN_ERROR.getMessage()));
            return;
        }
        chain.doFilter(request, response);
    }

}
