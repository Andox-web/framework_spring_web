package mg.itu.prom16.mapping;

import java.lang.annotation.Annotation;
import java.util.Map;

import mg.itu.prom16.exception.build.BuildException;
import mg.itu.prom16.util.PackageScanner;

public class Mapper {
    Map<VerbMapping,Mapping> controllerList;

    public Mapper(String controllerPackage,Class<? extends Annotation > annotationController) throws BuildException, ClassNotFoundException{
        controllerList=PackageScanner.getMapping(controllerPackage, annotationController);
    }
    public Mapping get(VerbMapping verbMapping){
        return controllerList.get(verbMapping);
    }
}
