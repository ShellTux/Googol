ARCHIVE       = ./Googol-LuísGóis.zip
JAVADOC_DIR   = ./target/reports/apidocs/
JAVADOC_FILES = $(shell find $(JAVADOC_DIR) -type f)
JAVA_SRCS     = $(shell find src -type f -name "*.java")
JAVA_TARGET   = ./target/googol-1.0-SNAPSHOT.jar

$(JAVA_TARGET): $(JAVA_SRCS)
	mvn package

.PHONY: javadoc
javadoc:
	mvn javadoc:javadoc

.PHONY: archive
archive: $(ARCHIVE) | javadoc
	rm $<
	git ls-files > files.txt
	find $(JAVADOC_DIR) -type f >> files.txt
	zip $< -@ < files.txt
