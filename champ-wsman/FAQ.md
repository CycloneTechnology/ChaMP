#FAQ

**Q** Get error: Cannot find key of appropriate type to decrypt AP REP - RC4 with HMAC

**A** Check that have correct user (configured as indicated above) in the credentials. Keytab is (e.g. type 17 AES128 SHA1)
but token received is type 23 (RC4 HMAC). 
May take some time for this to take effect (something is cached somewhere. Where???)

**Q** Defective token detected (Mechanism level: Invalid padding on Wrap Token)

**A** When trying to decode received data. Java bug with RC4 HMAC - hence cannot use this encryption type.

