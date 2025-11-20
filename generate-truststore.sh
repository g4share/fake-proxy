keytool -importcert \
  -file littleproxy-mitm.pem \
  -alias fp2-ca \
  -keystore truststore-fp2.jks \
  -storepass changeit \
  -noprompt