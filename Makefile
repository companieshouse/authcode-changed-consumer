SHELL := /bin/bash

artifact_name ?= $(shell sed -n 's|^[[:space:]]*<artifactId>\(.*\)</artifactId>|\1|p' pom.xml | head -n1)
version       ?= $(shell sed -n 's|^[[:space:]]*<version>\(.*\)</version>|\1|p' pom.xml | head -n1)

jar_target    := target/$(artifact_name)-$(version).jar
out_jar       := target/$(artifact_name).jar

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(out_jar)
	rm -f ./$(artifact_name)-*.zip
	rm -rf ./build-*
	rm -f ./build.log

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	cp $(jar_target) ./$(out_jar)

.PHONY: test
test: test-unit test-integration

.PHONY: test-unit
test-unit: clean
	mvn test

.PHONY: test-integration
test-integration: clean
	mvn integration-test -Dskip.unit.tests=true failsafe:verify

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	@echo "Packaging version: $(version)"
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp $(jar_target) $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis

.PHONY: security-check
security-check:
	mvn org.owasp:dependency-check-maven:update-only
	mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=4 -DassemblyAnalyzerEnabled=false
