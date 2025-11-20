./mvnw clean install

mkdir -p ~/fake-proxy
rm -rf ~/fake-proxy/*

cp target/fake-proxy-1.0-dist.zip ~/fake-proxy
unzip target/fake-proxy-1.0-dist.zip -d ~/fake-proxy

cp generate-truststore.sh ~/fake-proxy/fake-proxy-1.0
