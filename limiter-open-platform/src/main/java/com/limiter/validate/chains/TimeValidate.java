package com.limiter.validate.chains;

import com.limiter.TokenBucketContainer;
import com.limiter.common.utils.TokenBucketKeyUtils;
import com.limiter.validate.ErrorInfo;
import com.limiter.validate.error.ErrorInfoFactory;

/**
 * 调用次数验证器
 *
 * @author wuhao
 */
public class TimeValidate implements Validate {

    private ErrorInfoFactory errorInfoFactory;

    public void setErrorInfoFactory(ErrorInfoFactory errorInfoFactory) {
        this.errorInfoFactory = errorInfoFactory;
    }

    @Override
    public void doNextValidate(ValidateContext context, ValidateHandlerChain handlerChain) {
        String appkey = context.getAppKey();
        String method = context.getMethod();

        String tokenBucketKey = TokenBucketKeyUtils.generateTokenBucketKey(appkey, method);
        TokenBucketContainer tokenBucketContainer = new TokenBucketContainer(tokenBucketKey);
        boolean success = tokenBucketContainer.tryConsume();
        if (success) {
            handlerChain.doHandle(context);
        } else {
            doFailed(context);
        }
    }

    private void doFailed(ValidateContext context) {
        context.setSuccess(false);
        ErrorInfo errorInfo = errorInfoFactory.getErrorInfo(context.getAppKey(), context.getMethod());
        context.setMessage(errorInfo);
    }

}
