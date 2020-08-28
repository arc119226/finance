package module.io
@Grab(group='commons-io', module='commons-io', version='2.5')

import org.apache.commons.io.FileUtils
class Batch{

	def logPath='log'

	def command = []
	def prop = [:]
	
	def propertyMissing(String name) {
		return prop[name]
	}

	def methodMissing(String name, def args) {
		if(args.length==1){

			def storage = [:]
			storage[name]=args[0]
			command << storage
		}else{
			def storage = [:]
			storage[name]=args
			command << storage
		}
        return command
   }

	def executbat(){
		while(command.size()>0){
		 	this.prop=command[0]
			if(write){
				if(write.getClass().isArray()){
					new FileOutputStream("${write[0]}").withWriter("${write[1]}") { writer ->
						for(int i=2;i<write.length;i++){
	                		writer << "${write[i]}"
	                	}
	            	}
	        	}
	        	prop.remove(write)
			}

			if(mkdirs){
				if(mkdirs.getClass().isArray()){
					mkdirs.each{
						if(!new File("${it}").exists()){
							new File("${it}").mkdirs()
						}
					}
				}else{
					if(!new File("${mkdirs}").exists()){
						new File("${mkdirs}").mkdirs()
					}
				}
				prop.remove(mkdirs)
			}

			if(rename){
				if(rename.getClass().isArray()){
					new File(rename[0]).renameTo(rename[1])
				}
				prop.remove(rename)
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
				prop.remove(clean)
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
				prop.remove(delete)
			}

			if(err){
				def date = new Date()
				def yyyymmdd=date.format('yyyyMMdd')
				def yyyymmddhhmmss=date.format('yyyy-MM-dd HH:mm:ss.SSS')
				println "${yyyymmddhhmmss}\terror\t${err}\n"
				if(new File("./${logPath}").exists()){
	            	File errormsg = new File("./${logPath}/${yyyymmdd}.error.log")
	            	errormsg << "${yyyymmddhhmmss}\terror\t${err}\n"
				}else{
					new File("./${logPath}").mkdirs()
					File errormsg = new File("./${logPath}/${yyyymmdd}.error.log")
	            	errormsg << "${yyyymmddhhmmss}\terror\t${err}\n"
				}
				prop.remove(err)
			}

			if(info){
				def date = new Date()
				def yyyymmdd=date.format('yyyyMMdd')
				def yyyymmddhhmmss=date.format('yyyy-MM-dd HH:mm:ss.SSS')
				print "${yyyymmddhhmmss}\tinfo\t${info}\n"
				if(new File("./${logPath}").exists()){
					File logmsg = new File("./${logPath}/${yyyymmdd}.log")
	            	logmsg << "${yyyymmddhhmmss}\tinfo\t${info}\n"
	            }else{
	            	new File("./${logPath}").mkdirs()
	            	File logmsg = new File("./${logPath}/${yyyymmdd}.log")
	            	logmsg << "${yyyymmddhhmmss}\tinfo\t${info}\n"
	            }
	            prop.remove(info)
			}
			command.remove(0)
		}//while
	}//executebat



	def static exec(@DelegatesTo(Batch) Closure block){
		Batch m = new Batch()
		block.delegate = m
		block()
		m.executbat()
	}
}