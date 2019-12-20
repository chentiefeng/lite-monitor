package me.ctf.lm.config;

import me.ctf.lm.dto.MapResult;
import me.ctf.lm.util.BizException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 14:35
 */
@RestControllerAdvice
public class ExceptionHandlers {
    /**
     * json 格式错误验证
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public MapResult errorHandler(MethodArgumentNotValidException ex) {
        StringBuilder errorMsg = new StringBuilder();
        BindingResult re = ex.getBindingResult();
        for (ObjectError error : re.getAllErrors()) {
            errorMsg.append(error.getDefaultMessage()).append(",");
        }
        errorMsg.delete(errorMsg.length() - 1, errorMsg.length());
        return MapResult.error(errorMsg.toString());
    }


    /**
     * 表单异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public MapResult errorHandler(BindException ex) {
        BindingResult result = ex.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        for (ObjectError error : result.getAllErrors()) {
            errorMsg.append(error.getDefaultMessage()).append(",");
        }
        errorMsg.delete(errorMsg.length() - 1, errorMsg.length());
        return MapResult.error(errorMsg.toString());
    }

    /**
     * 表单异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = BizException.class)
    public MapResult errorHandler(BizException ex) {
        return MapResult.error(ex.getCode(), ex.getMsg());
    }
}
