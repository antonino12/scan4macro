package scan4macro;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TypesExtractor extends VoidVisitorAdapter<Object>{
    
    class Pair {
        String nodeName;
        int nodeNumber;
        Pair(String nn,int no) {
            nodeName = nn;
            nodeNumber = no;
        }
    }
    
    protected Vector<Pair> methodsList = new Vector<Pair>();
    protected Vector<Pair> allImportList = new Vector<Pair>();
    protected Vector<Pair> classImportList = new Vector<Pair>();
    protected Vector<Pair> langImportList = new Vector<Pair>();
    
    protected Vector<Pair> unsafeList = new Vector<Pair>();
    
    protected File file = null;
    
    public TypesExtractor(File f) {
        file = f;
    }
    
    @Override
    public void visit(MethodCallExpr n, Object arg) {
        super.visit(n, arg);
        String nn = n.getName().replaceAll("\\(.*\\)", "");
        //System.out.println(" [L " + n.getBeginLine() + "] " + n + " ::<< " + nn);
        char c = n.getParentNode().toString().charAt(0);
        if( c>='A' && c<='Z' ) {
            String clsName = n.getParentNode().toString().split("\\.")[0];
            addVarType(clsName,n.getParentNode().getBeginLine());
        }
        if( WhiteClasses.isSafeMethod(n.toString()) == WhiteClasses.WHITE_LISTED_NO ) {
            unsafeList.add(new Pair(n.toString(),n.getBeginLine()));
        }
        String ss = "java.lang.System." + nn;
        String tt = "java.io.File." + nn;
        if( WhiteClasses.isSafeMethod(ss) == WhiteClasses.WHITE_LISTED_NO ) {
            unsafeList.add(new Pair(ss,n.getBeginLine()));
        }else if( WhiteClasses.isSafeMethod(tt) == WhiteClasses.WHITE_LISTED_NO ) {
            unsafeList.add(new Pair(tt,n.getBeginLine()));
        }
        
        if( n.toString().contains(".") ) {
            String[] obj =  n.toString().split("\\.");
            if( obj[0].equals("System") && (obj[1].equals("out")||obj[1].equals("in")||obj[1].equals("err")) ) {
                unsafeList.add(new Pair(n.toString(),n.getBeginLine()));
            }
        }
    }
    
    @Override
    public void visit(ReferenceType n,Object arg) {
        super.visit(n,arg);
        //System.out.println("Cll [L " + n.getBeginLine() + "] " + n + " ::<< ");
    }
    
    @Override
    public void visit(ImportDeclaration n,Object arg) {
        super.visit(n,arg);
        String packName = n.getName().toString();
        if( packName.equals(packName.toLowerCase()) ) { // check if contains all classes
            allImportList.add(new Pair(packName,n.getBeginLine()));
        }else {
            classImportList.add(new Pair(packName,n.getBeginLine()));
        }
    }
    
    protected void addVarType(String typeName,int line) {
        if( !typeName.contains(".") ) {
            langImportList.add(new Pair(typeName,line)); // default 
        }else {
            classImportList.add(new Pair(typeName,line));
        }
    }
    
    @Override
    public void visit(VariableDeclarationExpr n,Object arg) {
        super.visit(n,arg);
        //n.getType().getAnnotations().get(index)
        String packName = n.getType().toString();
        addVarType(packName,n.getBeginLine());
        //System.out.println("VariableDeclarationExpr [L " + packName + "] " + n);
    }
    
    @Override
    public void visit(MethodDeclaration n,Object arg) {
        super.visit(n,arg);
        //n.getParameters().get(0).getType()
        List<Parameter> ann = n.getParameters();
        if( !n.getType().toString().equals("void") ) {
            addVarType(n.getType().toString(),n.getType().getBeginLine());            
        }
        if( null == ann ) return ;
        for( int i=0; i<ann.size(); i++ ) {
            addVarType(ann.get(i).getType().toString(),ann.get(i).getType().getBeginLine());
            //System.out.println(":::"+ann.get(i).getType().toString());
        }
    }
    
    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        List<ClassOrInterfaceType> imps = n.getImplements();
        for( int i=0; imps!=null&&i<imps.size(); i++ ) {
            String className = imps.get(i).toString();
            //System.out.println("Class name====> :: " + className);
            addVarType(className,imps.get(i).getBeginLine());
        }
    }
    
    @Override
    public void visit(com.github.javaparser.ast.expr.ObjectCreationExpr n, Object arg) {
        //System.out.println("Class name====> :: " + n.getType().toString());
        addVarType(n.getType().toString(),n.getType().getBeginLine());
    }
    
    @Override
    public void visit(TryStmt n, Object arg) {
        super.visit(n, arg);
        for( int i=0; i<n.getCatchs().size(); i++ ) {
            List<Type> types = n.getCatchs().get(i).getExcept().getTypes();
            for( int j=0; j<types.size(); j++ ) {
                addVarType(types.get(j).toString(),types.get(j).getBeginLine());
            }
        }
    }
    
    public void extractAll() {
        try {
            this.visit(JavaParser.parse(file), null);
        } catch (ParseException ex) {
            Logger.getLogger(TypesExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TypesExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for( int i=0; i<allImportList.size(); i++ ) {
            if( WhitePackages.getInstance().isSafePackage(allImportList.get(i).nodeName) == WhitePackages.WHITE_LISTED_NO ) {
                unsafeList.add(allImportList.get(i));
            }
        }
        for( int i=0; i<classImportList.size(); i++ ) {
            int type = WhitePackages.getInstance().isSafePackage(Utils.getSubPackage(classImportList.get(i).nodeName));
            if( type == WhitePackages.WHITE_LISTED_ENTIRELY )
                continue;
            else if( type == WhitePackages.WHITE_LISTED_YES ){
                if( WhiteClasses.isSafeClass(classImportList.get(i).nodeName,classImportList.get(i),false) != WhiteClasses.WHITE_LISTED_NO ) {
                    continue;
                }
            }
            unsafeList.add(classImportList.get(i));
        }
        
        for( int i=0; i<langImportList.size(); i++ ) {
            if( WhiteClasses.isSafeClass(langImportList.get(i).nodeName,langImportList.get(i),true) == WhiteClasses.WHITE_LISTED_NO ) {//"java.lang."+
                unsafeList.add(langImportList.get(i));
            }
        }
    }
    


    public static void main(String[] args) {
        File csvFiles = new File(args[0]);
        if( csvFiles.isDirectory() ) {
            File[] csvs = csvFiles.listFiles();
            for( int i=0; i<csvs.length; i++ ) {
                if( !csvs[i].getName().endsWith(".csv") )
                    continue;
                if( csvs[i].getName().contains(" ") ) {
                    //System.out.println(csvs[i].getPath());
                    new WhitePackages(csvs[i].getPath());
                }else {
                    new WhiteClasses(csvs[i].getPath(),csvs[i].getName().replace("csv", ""));
                }
            }
        }
        
        File file = new File(args[1]);
        TypesExtractor te = new TypesExtractor(file);
        te.extractAll();
        
        if( te.unsafeList.size() == 0 ) {
            System.out.println("macro is safe");   
            //System.out.println(String.format("%d", scanner.getUnsafeLines()));           
        }else {
            System.out.println("macro contains unsafe commands");   
            System.out.println(String.format("number of unsafed command lines: %d", te.unsafeList.size())); 
            for( int i=0; i<te.unsafeList.size(); i++ ) {
                Pair p = te.unsafeList.get(i);
                System.out.println("CLASS NOT WHITELISTED---LINE-"+p.nodeNumber+"---");
                String[] tags = p.nodeName.split("\\.");
                String subPack = "";
                for( int j=0; j<tags.length-1; j++ )  {
                    if( j>0 ) subPack += ".";
                    subPack += tags[j];
                }
                System.out.println(" \tPackage:\"" + tags[0] + "\",Sub Package:\""+subPack+"\",Class:\""+tags[tags.length-1]+"\"");
            }
        }
        
    }
}
