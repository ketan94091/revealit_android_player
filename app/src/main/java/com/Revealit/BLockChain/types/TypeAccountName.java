package com.Revealit.BLockChain.types;

import com.Revealit.BLockChain.util.StringUtils;

public class TypeAccountName extends TypeName {
    private static final int MAX_ACCOUNT_NAME_LEN = 12;

    private static final char CHAR_NOT_ALLOWED = '.';

    public TypeAccountName(String name) {
        super(name);

        if (! StringUtils.isEmpty( name )) {
            if (name.length() > MAX_ACCOUNT_NAME_LEN ) {
                throw new IllegalArgumentException("account name can only be 12 chars long: " + name) ; // changed from dawn3
            }

            if ( (name.indexOf( CHAR_NOT_ALLOWED) >= 0) && ! name.startsWith("eosio.") ){
                throw new IllegalArgumentException("account name must not contain '.': " + name);
            }
        }
    }
}
