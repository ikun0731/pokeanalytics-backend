package com.pokeanalytics.userteamservice.exception;

import com.pokeanalytics.userteamservice.common.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 负责捕获并处理应用中抛出的各种异常，统一异常响应格式
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理用户已存在异常
     *
     * @param ex 用户已存在异常
     * @return 包含错误信息的响应对象
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict 更符合语义
    public ResultVO<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.warn(ex.getMessage());
        return ResultVO.fail(409, ex.getMessage());
    }

    /**
     * 处理参数验证失败异常
     *
     * @param ex 参数验证失败异常
     * @return 包含错误信息的响应对象
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request
    public ResultVO<String> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", errorMessage);
        return ResultVO.fail(400, errorMessage);
    }

    /**
     * 处理全局未捕获异常
     *
     * @param ex 任何未被其他处理器捕获的异常
     * @return 包含错误信息的响应对象
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO<String> handleGlobalException(Exception ex) {
        log.error("An unexpected error occurred: ", ex);
        return ResultVO.fail(500, "服务器内部错误，请联系管理员！");
    }

    /**
     * 处理凭证错误异常
     *
     * @param ex 凭证错误异常
     * @return 包含错误信息的响应对象
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
    public ResultVO<String> handleBadCredentialsException(BadCredentialsException ex) {
        // 注意：为了安全，不要返回过于详细的错误信息，比如“密码错误”或“用户不存在”
        return ResultVO.fail(401, "用户名或密码错误");
    }
    /**
     * 处理无效验证码异常
     *
     * @param ex 无效验证码异常
     * @return 包含错误信息的响应对象
     */
    @ExceptionHandler(InvalidCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request
    public ResultVO<String> handleInvalidCodeException(InvalidCodeException ex) {
        log.warn(ex.getMessage());
        return ResultVO.fail(400, ex.getMessage());
    }


    /**
     * 处理用户名不存在异常
     *
     * @param ex 用户名不存在异常
     * @return 包含错误信息的响应对象
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404 Not Found
    public ResultVO<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn(ex.getMessage());
        return ResultVO.fail(404, ex.getMessage());
    }
    /**
     * 处理禁止访问异常
     *
     * @param ex 禁止访问异常
     * @return 包含错误信息的响应对象
     */
    @ExceptionHandler(ForbiddenAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403 Forbidden
    public ResultVO<String> handleForbiddenAccessException(ForbiddenAccessException ex) {
        log.warn(ex.getMessage());
        return ResultVO.fail(403, ex.getMessage());
    }
    /**
     * 处理无效密码异常
     *
     * @param ex 无效密码异常
     * @return 包含错误信息的响应对象
     */
    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request
    public ResultVO<String> handleInvalidPasswordException(InvalidPasswordException ex) {
        log.warn(ex.getMessage());
        return ResultVO.fail(400, ex.getMessage());
    }
}