package jp.bizreach.twitter.domainfw.action;

import jp.bizreach.twitter.projectfw.web.action.TwitterMessages;
import org.apache.struts.action.ActionMessage;

/**
 * The keys for message.
 * @author FreeGen
 */
public class NisetterMessages extends TwitterMessages {

    private static final long serialVersionUID = 1L;

    /** The key of the message: ユーザ名とパスワードに利用できる文字は半角英数字のみになっていることを、ご了承していただけませんでしょうか。 */
    public static final String ERRORS_SIGNUP_ILLEGALCHAR = "errors.signup.illegalchar";

    /** The key of the message: 例: 290-9753 */
    public static final String MESSAGES_ZIP_CODE_INPUT_EXAMPLE = "messages.zipCode.input.example";

    /**
     * Add the created action message for the key 'errors.empty.login' with parameters.
     * <pre>
     * message: ユーザ名またはパスワードが空白である可能性がゼロではありません
     * comment: @Override ----------------
     * </pre>
     * @param property The property name for the message. (NotNull)
     */
    @Override
    public void addErrorsEmptyLogin(String property) {
        assertPropertyNotNull(property);
        add(property, new ActionMessage(ERRORS_EMPTY_LOGIN, (Object[])null));
    }

    /**
     * Add the created action message for the key 'errors.not.login' with parameters.
     * <pre>
     * message: ユーザ名とパスワードが誤っている可能性がゼロではありません
     * comment: @Override
     * </pre>
     * @param property The property name for the message. (NotNull)
     */
    @Override
    public void addErrorsNotLogin(String property) {
        assertPropertyNotNull(property);
        add(property, new ActionMessage(ERRORS_NOT_LOGIN, (Object[])null));
    }

    /**
     * Add the created action message for the key 'errors.signup.illegalchar' with parameters.
     * <pre>
     * message: ユーザ名とパスワードに利用できる文字は半角英数字のみになっていることを、ご了承していただけませんでしょうか。
     * </pre>
     * @param property The property name for the message. (NotNull)
     */
    public void addErrorsSignupIllegalchar(String property) {
        assertPropertyNotNull(property);
        add(property, new ActionMessage(ERRORS_SIGNUP_ILLEGALCHAR, (Object[])null));
    }

    /**
     * Add the created action message for the key 'messages.zipCode.input.example' with parameters.
     * <pre>
     * message: 例: 290-9753
     * comment: ----------
     * </pre>
     * @param property The property name for the message. (NotNull)
     */
    public void addMessagesZipCodeInputExample(String property) {
        assertPropertyNotNull(property);
        add(property, new ActionMessage(MESSAGES_ZIP_CODE_INPUT_EXAMPLE, (Object[])null));
    }

    /**
     * The definition of keys for labels.
     */
    public static interface LabelKey extends TwitterMessages.LabelKey {

        /** The key of the label: 郵便番号 */
        String LABELS_ZIP_CODE = "labels.zipCode";
    }
}
