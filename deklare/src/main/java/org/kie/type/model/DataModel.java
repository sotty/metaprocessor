package org.kie.type.model;

import com.sun.javaws.jnl.PackageDesc;
import org.kie.type.annotations.processors.ProcessorRegistry;
import org.kie.type.utils.HierarchySorter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DataModel {

    private Map<String,PackageDescriptor> packages;

    public DataModel() {
        this.packages = new HashMap<String, PackageDescriptor>( 3 );
    }

    public void addPackage( String packName, PackageDescriptor descr ) {
        if ( ! this.packages.containsKey( packName ) ) {
            this.packages.put( packName, descr );
        } else {
            PackageDescriptor original = this.packages.get( packName );
            original.merge( descr );
        }
    }

    public void process( ProcessorRegistry registry ) {

        sortPackages( packages );

        for ( PackageDescriptor pd : packages.values() ) {
            pd.init( registry );
        }
    }

    private void sortPackages( Map<String, PackageDescriptor> packages ) {
        Map<String,Collection<String>> inheritance = new HashMap<String,Collection<String>>();

        for ( PackageDescriptor pd : packages.values() ) {
            for ( TypeDescriptor td : pd.getTypes().values() ) {
                inheritance.put( td.getName(), new HashSet<String>( td.getSourceAST().getInterfaces() ) );
            }
        }

        final List<String> sortedTypes = new HierarchySorter<String>().sort( inheritance );

        for ( PackageDescriptor pd : packages.values() ) {
            List<TypeDescriptor> typeList = new ArrayList<TypeDescriptor>( pd.getTypes().values() );
            Collections.sort( typeList, new Comparator<TypeDescriptor>() {
                @Override
                public int compare( TypeDescriptor td1, TypeDescriptor td2 ) {
                    return sortedTypes.indexOf( td1.getName() ) - sortedTypes.indexOf( td2.getName() );
                }
            });
            pd.getTypes().clear();
            for ( TypeDescriptor td : typeList ) {
                pd.add( td.getName(), td );
            }
        }
    }

    @Override
    public String toString() {
        return "DataModel{" +
               "packages=" + packages +
               '}';
    }
}
