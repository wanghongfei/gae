package org.fh.gae.net.error;

public class GaeException extends RuntimeException {
    private int code;

    public GaeException() {
        super(ErrCode.SUCCESS.msg());
        this.code = ErrCode.SUCCESS.code();
    }

    public GaeException(ErrCode errCode) {
        super(errCode.msg());
        this.code = errCode.code();
    }


    public GaeException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int code() {
        return this.code;
    }
}
