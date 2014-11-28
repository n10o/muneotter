package jp.bizreach.twitter.domainfw.direction;

import jp.bizreach.twitter.projectfw.core.direction.TwitterConfig;

/**
 * @author FreeGen
 */
public interface NisetterConfig extends TwitterConfig {

    /** The key of the configuration. e.g. Nisetter */
    String DOMAIN_TITLE = "domain.title";

    /** The key of the configuration. e.g. DCK */
    String COOKIE_AUTO_LOGIN_NISETTER_KEY = "cookie.auto.login.nisetter.key";

    /** The key of the configuration. e.g. 10 */
    String PAGING_PAGE_RANGE_SIZE = "paging.page.range.size";

    /**
     * Get the value of property as {@link String}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String get(String propertyKey);

    /**
     * Is the property true?
     * @param propertyKey The key of the property which is boolean type. (NotNull)
     * @return The determination, true or false. (if the property can be true, returns true)
     */
    boolean is(String propertyKey);

    /**
     * Get the value for the key 'domain.title'. <br />
     * The value is, e.g. Nisetter <br />
     * comment: @Override The title of domain the application for logging
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getDomainTitle();

    /**
     * Get the value for the key 'cookie.auto.login.nisetter.key'. <br />
     * The value is, e.g. DCK <br />
     * comment: The cookie key of auto-login for Nisetter
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getCookieAutoLoginNisetterKey();

    /**
     * Get the value for the key 'paging.page.range.size'. <br />
     * The value is, e.g. 10 <br />
     * comment: @Override The size of page range for paging
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    String getPagingPageRangeSize();

    /**
     * Get the value for the key 'paging.page.range.size' as {@link Integer}. <br />
     * The value is, e.g. 10 <br />
     * comment: @Override The size of page range for paging
     * @return The value of found property. (NullAllowed: if null, not found)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPagingPageRangeSizeAsInteger();

    /**
     * The simple implementation for configuration.
     */
    public static class SimpleImpl extends TwitterConfig.SimpleImpl implements NisetterConfig {

        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDomainTitle() {
            return get(NisetterConfig.DOMAIN_TITLE);
        }

        /**
         * {@inheritDoc}
         */
        public String getCookieAutoLoginNisetterKey() {
            return get(NisetterConfig.COOKIE_AUTO_LOGIN_NISETTER_KEY);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getPagingPageRangeSize() {
            return get(NisetterConfig.PAGING_PAGE_RANGE_SIZE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Integer getPagingPageRangeSizeAsInteger() {
            return getAsInteger(NisetterConfig.PAGING_PAGE_RANGE_SIZE);
        }
    }
}
