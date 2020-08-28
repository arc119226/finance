package module.io
@Grab(group='commons-io', module='commons-io', version='2.5')

import org.apache.commons.io.FileUtils
class FileBetch{
	def errorPath='error',logPath='log'

	def errorPath(def errorPath){
		this.errorPath=errorPath
	}
	def logPath(def logPath){
		this.logPath=logPath
	}

	def storage = [:]

	def propertyMissing(String name) { 
		return storage[name] 
	}

	def methodMissing(String name, def args) {
		if(args.length==1){
			storage[name]=args[0]
		}else{
			storage[name]=args
		}
        return storage[name]
   }

	def executer(){
		if(write){
				if(outputstream.getClass().isArray()){
					new FileOutputStream("${outputstream[0]}").withWriter("${outputstream[1]}") { writer ->
						for(i=2;i<outputstream.length;i++){
	                		writer << outputstream[i]
	                	}
	            	}
        		}
		}
		if(mkdirs){
			if(mkdirs.getClass().isArray()){
				mkdirs.each{
					new File("${it}").mkdirs()
				}
			}else{
				new File("${mkdirs}").mkdirs()
			}
		}
		if(rename){
			if(clean.getClass().isArray()){
				new File(rename[0]).renameTo(rename[1])
			}
		}
		if(clean){
			if(clean.getClass().isArray()){
				clean.each{
					if(new File("${it}").exists()){
						FileUtils.cleanDirectory(new File("${it}"))
					}
				}
			}else{
				if(new File("${clean}").exists()){
					FileUtils.cleanDirectory(new File("${clean}"))
				}
			}
		}
		if(delete){
			if(delete.getClass().isArray()){
				delete.each{
					if(new File("${it}").exists()){
						new File("${it}").delete()
					}
				}
			}else{
				if(new File("${delete}").exists()){
					new File("${delete}").delete()
				}
			}

		}
		if(error){
			def date = new Date()
			def yyyymmdd=date.format('yyyyMMdd')
			def yyyymmddhhmmss=date.format('yyyy-MM-dd HH:mm:ss.SSS')
			println "${yyyymmddhhmmss}\terror\t${error}\n"
			if(new File("./${errorPath}").exists()){
            	File errormsg = new File('./error/${yyyymmdd}.err')
            	errormsg << "${yyyymmddhhmmss}\terror\t${error}\n"
			}else{
				new File("./${errorPath}").mkdirs()
			}

		}
		if(info){
			def date = new Date()
			def yyyymmdd=date.format('yyyyMMdd')
			def yyyymmddhhmmss=date.format('yyyy-MM-dd HH:mm:ss.SSS')
			print "${yyyymmddhhmmss}\tinfo\t${info}\n"
			if(new File("./${logPath}").exists()){
				File logmsg = new File("./${yyyymmdd}.log")
            	logmsg << "${yyyymmddhhmmss}\tinfo\t${info}\n"
            }else{
            	new File("./${logPath}").mkdirs()
            }
		}
	}
	def static execute(@DelegatesTo(FileBetch) Closure block){
		FileBetch m = new FileBetch()
		block.delegate = m
		block()
		m.executer()
	}
}