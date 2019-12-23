package me.ctf.lm.util.validator;

import me.ctf.lm.util.BizException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 15:22
 */
@Component
public class ValidatorUtil {
    private static Validator validator;

    @Resource
    public void setValidator(Validator validator) {
        ValidatorUtil.validator = validator;
    }

    /**
     * 校验对象
     *
     * @param object 待校验对象
     * @param groups 待校验的组
     * @throws BizException 校验不通过，则报BizException异常
     */
    public static void validateEntity(Object object, Class<?>... groups)
            throws BizException {
        Class<?>[] realGroups = new Class<?>[groups.length + 1];
        System.arraycopy(groups, 0, realGroups, 0, groups.length);
        realGroups[groups.length] = Default.class;
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, realGroups);
        if (!constraintViolations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for (ConstraintViolation<Object> constraint : constraintViolations) {
                msg.append(constraint.getMessage());
            }
            throw new BizException(msg.toString());
        }
    }
}
