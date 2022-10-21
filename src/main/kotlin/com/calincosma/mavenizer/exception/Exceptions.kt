package com.calincosma.mavenizer.exception

class JarProcessingException : RuntimeException {
	constructor(message: String?) : super(message)
	constructor() : super()
	constructor(message: String?, e: Throwable?) : super(message, e)
}

class ConfigNotFoundException : RuntimeException {
	constructor(message: String?) : super(message) {}
	constructor() : super() {}
	constructor(message: String?, e: Throwable?) : super(message, e) {}
}

class ClassDependencyNotFoundException : RuntimeException {
	constructor(message: String?) : super(message) {}
	constructor() : super() {}
	constructor(message: String?, e: Throwable?) : super(message, e) {}
}
