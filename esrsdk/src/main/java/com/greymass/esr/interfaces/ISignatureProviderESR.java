package com.greymass.esr.interfaces;

import com.greymass.esr.models.Signature;

public interface ISignatureProviderESR {

    Signature sign(String message);

}
