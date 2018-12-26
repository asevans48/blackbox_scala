package tests

import java.io.{File, PrintWriter}
import java.nio.file.{Path, Paths}

import org.scalatest.{FlatSpec, Matchers}
import si.api.blackbox.stores.{KeyStore, OutputResult}


object TestObjects{

  def create_test_file(store_dir: String): File ={
    val store = new KeyStore(store_dir);
    var dirpath = Paths.get(store_dir, "test_file1.txt.gpg");
    store.removeFile(dirpath.toString);
    dirpath = Paths.get(store_dir, "test_file1.txt");
    store.removeFile(dirpath.toString);
    val fpath = Paths.get(store_dir, "test_file1.txt");
    val file = new File(fpath.toString)
    if(file.exists()){
      file.delete();
      store.removeFile(fpath.toString)
    }
    file.createNewFile();
    val pw = new PrintWriter(file);
    pw.write("test12345");
    pw.close()
    return file;
  }

}


class TestKeyStore  extends FlatSpec with Matchers{
  var home_dir = System.getProperty("user.home");
  home_dir = Paths.get(home_dir, "PycharmProjects", "KeyStore").toString;

  "A KeyStore" should "return a map of output results via ls" in {
    val store = new KeyStore(home_dir);
    val out_map = store.ls();
    out_map shouldBe an [OutputResult];
  }

  it should "return successfully when using ls in an existing blackbox directory" in {
    val store = new KeyStore(home_dir);
    val out_map = store.ls();
    out_map.success shouldBe true;
  }

  it should "contain an output list of file names from blackbox via ls" in{
    val store = new KeyStore(home_dir);
    val out_map = store.ls();
    out_map.output.isInstanceOf[Array[Path]] shouldBe true
  }

  it should "return a map of output results via diff " in {
    val store = new KeyStore(home_dir);
    val out_map = store.diff();
    out_map shouldBe an [OutputResult];
  }

  it should "register a new file in blackbox via addFile" in {
    val fpath: File = TestObjects.create_test_file(home_dir);
    val store = new KeyStore(home_dir);
    val out_map = store.addFile(fpath.getAbsolutePath);
    out_map.success shouldBe true;
    val ls_map = store.ls();
    val ls_list = ls_map.output.asInstanceOf[Array[Path]];
    val ls_bools = ls_list.map(x => x.compareTo(fpath.toPath) == 0);
    ls_bools should contain(true);
  }

  it should "deregister a blackbox file when requested" in {
    val fpath: File = TestObjects.create_test_file(home_dir);
    val store = new KeyStore(home_dir);
    val out_map = store.addFile(fpath.getAbsolutePath);
    print(fpath.getAbsolutePath)
    val result = store.removeFile(fpath.getAbsolutePath+".gpg");
    result.success shouldBe true;
  }

  it should "cat a file from blackbox" in {
    val fpath: File = TestObjects.create_test_file(home_dir);
    val store = new KeyStore(home_dir);
    val cat_output = store.catFile("test_file1.txt");
    cat_output.success shouldBe true;
    cat_output.output.asInstanceOf[String].trim should be ("test12345");
  }
}
