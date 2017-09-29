package it.publisys.pagamentionline.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mcolucci
 */
/*@Aspect
public class LoggingAspect {

    private static final Logger _log = LoggerFactory.getLogger(LoggingAspect.class);
    //
    private static final String AOP_EXEC = "execution(public * it.publisys.pagamentionline.controller..*.*(..))"
        + " && !execution(public * it.publisys.pagamentionline.controller.BaseController.*(..))";

    @AfterReturning(pointcut = AOP_EXEC, returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        _log.debug(joinPointToString("logAfterReturning()", joinPoint, result));
    }

    @AfterThrowing(pointcut = AOP_EXEC, throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        _log.debug(joinPointToString("logAfterThrowing()", joinPoint, error));
    }

    private String joinPointToString(String status,
                                     JoinPoint joinPoint, Object result) {
        return joinPointToString(status, joinPoint, result, null);
    }

    private String joinPointToString(String status,
                                     JoinPoint joinPoint, Throwable error) {
        return joinPointToString(status, joinPoint, null, error);
    }

    private String joinPointToString(String status,
                                     JoinPoint joinPoint, Object result,
                                     Throwable error) {
        StringBuilder _b = new StringBuilder("\n");

        _b.append("******** ").append(status).append(" is running! ********").append("\n");
        _b.append("Name: ").append(joinPoint.getSignature().getName()).append("\n");
        _b.append("Signature: ").append(joinPoint.getSignature().toLongString()).append("\n");
        _b.append("Args: ").append(Arrays.toString(joinPoint.getArgs())).append("\n");
        if (result != null) {
            _b.append("Result: ").append(result).append("\n");
        }
        if (error != null) {
            _b.append("Error: ").append(error).append("\n");
        }
        _b.append("****************************************");

        return _b.toString();
    }

}*/


/**
 * @author mcolucci
 */
@Aspect
public class LoggingAspect {
    private static final Logger _log = LoggerFactory.getLogger(LoggingAspect.class);
    //
    private static final String AOP_EXEC = "execution(public * it.publisys.pagamentionline.controller..*.*(..))"
            + " && !execution(public * it.publisys.pagamentionline.controller.BaseController.*(..))";

    /*
     @Before(AOP_EXEC)
     public void logBefore(JoinPoint joinPoint) {
     _log.debug(joinPointToString("logBefore()", joinPoint));
     }

     @After(AOP_EXEC)
     public void logAfter(JoinPoint joinPoint) {
     _log.debug(joinPointToString("logAfter()", joinPoint));
     }
     */
    @AfterReturning(pointcut = AOP_EXEC, returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        _log.debug(joinPointToString("logAfterReturning()", joinPoint, result));
    }

    @AfterThrowing(pointcut = AOP_EXEC, throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        _log.debug(joinPointToString("logAfterThrowing()", joinPoint, error));
    }

//    private String joinPointToString(String status, JoinPoint joinPoint) {
//        return joinPointToString(status, joinPoint, null);
//    }

    private String joinPointToString(String status,
                                     JoinPoint joinPoint, Object result) {
        return joinPointToString(status, joinPoint, result, null);
    }

    private String joinPointToString(String status,
                                     JoinPoint joinPoint, Throwable error) {
        return joinPointToString(status, joinPoint, null, error);
    }

    private String joinPointToString(String status,
                                     JoinPoint joinPoint, Object result,
                                     Throwable error) {
        StringBuilder _b = new StringBuilder("\n");

        _b.append("******** ").append(status).append(" is running! ********").append("\n");
        _b.append("Name: ").append(joinPoint.getSignature().getName()).append("\n");
        _b.append("Signature: ").append(joinPoint.getSignature().toLongString()).append("\n");
        _b.append("Args: ").append(Arrays.toString(joinPoint.getArgs())).append("\n");
        if (result != null) {
            _b.append("Result: ").append(result).append("\n");
        }
        if (error != null) {
            _b.append("Error: ").append(error).append("\n");
        }
        _b.append("****************************************");

        return _b.toString();
    }

}

