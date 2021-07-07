#!/bin/bash

keytool -genkeypair \
    -keyalg EC \
    -keysize 256 \
    -sigalg SHA256withECDSA \
    -validity 365 \
    -alias 'private' \
    -storetype pkcs12 \
    -keystore src/test/resources/keystore/sample.jks \
    -storepass 'N5^X[hvG' \
    -dname 'CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown'