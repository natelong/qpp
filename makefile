all: compile archive

compile:
	mkdir target
	javac src/net/natelong/qpp/App.java -d target

archive:
	jar cfm qpp.jar qpp.mf -C target .

clean:
	rm -rf target qpp.jar