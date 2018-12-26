package si.api.blackbox.stores

import java.io.File
import java.nio.file.Paths

import scala.sys.process._


case class OutputResult(success: Boolean, output: Any=null, error: String=null)


class KeyStore(key_store_dir: String){
  /**
    *  Key store written for blackbox.
    *
    *  @author aevans
    */


  /**
    * List files in the key store directory
    *
    * @return  An output result object
    */
  def ls(append_slash:Boolean=true): OutputResult = {
    try {
      val result: String = Process("blackbox_list_files", new File(key_store_dir)).!!;
      var files = result.split('\n')
      if(append_slash){
        files = files.map(x =>"/"+x);
      }
      val map_files = files.map(x => Paths.get(x));
      return OutputResult(true, output=map_files);
    }catch{
      case e: Exception =>
        return OutputResult(false, error=e.getMessage);
    }
  }

  /**
    * Diff encrypted v. decrypted vfiles on blackbox
    *
    * @return  The output result
    */
  def diff(): OutputResult = {
    try{
      val result: String = Process("blackbox_dif", new File(key_store_dir)).!!;
      return OutputResult(true, output=result);
    }catch{
      case e: Exception =>
        return OutputResult(false, error=e.getMessage);
    }
  }

  /**
    * Shreds and deletes encrypted files
    *
    * @return The results of shred in an OutputResult
    */
  def shred(): OutputResult = {
    try {
      val result: String = Process("blackbox_shred_all_files", new File(key_store_dir)).!!;
      return OutputResult(true, output=result);
    }catch{
      case e: Exception =>
        return OutputResult(false, error=e.getMessage)
    }
  }

  /**
    * Start editing a file
    *
    * @param fname  The file name to edit in a string as presented in blackbox
    * @return Any results in an OutputResult
    */
  def startEdit(fname: String): OutputResult = {
    try{
      val cmd = s"blackbox_edit_start $fname";
      val result: String = Process(cmd, new File(key_store_dir)).!!;
      return OutputResult(true, output=result);
    }catch{
      case e: Exception =>
        return OutputResult(false, error=e.getMessage);
    }

  }

  /**
    * Complete a file edit
    *
    * @param fname  The file name to edit in a string as presented in blackbox
    * @return A mapping of results
    */
  def endEdit(fname: String): OutputResult = {
    try {
      val cmd = s"blackbox_edit_end $fname";
      val result: String = Process(cmd, new File(key_store_dir)).!!;
      return OutputResult(true, output=result);
    }catch{
      case e: Exception =>
        return OutputResult(false, error=e.getMessage);
    }
  }

  /**
    * Remove a file
    *
    * @param fname  The file name to edit in a string as presented in blackbox
    * @return A mapping of results
    */
  def removeFile(fname: String): OutputResult = {
    try {
      val cmd = s"blackbox_deregister_file $fname";
      val result: String = Process(cmd, new File(key_store_dir)).!!;
      return OutputResult(true, output = result);
    }catch{
      case e: Exception =>
        return OutputResult(false, error=e.getMessage)
    }
  }

  /**
    * Register a file on blackbox
    *
    * @param fname  The file name to add in a string as presented in blackbox
    * @return A mapping of results
    */
  def addFile(fname: String): OutputResult = {
    try {
      val cmd = s"blackbox_register_new_file $fname";
      val result: String = Process(cmd, new File(key_store_dir)).!!;
      return OutputResult(true, output=result);
    }catch{
      case e: Exception =>
        return OutputResult(false, error=e.getMessage);
    }
  }

  /**
    * Obtain the file lines
    *
    * @param fname  The file name to edit in a string as presented in blackbox
    * @return A mapping of results
    */
  def catFile(fname: String): OutputResult= {
    try {
      val cmd = s"blackbox_cat $fname";
      val result: String = Process(cmd, new File(key_store_dir)).!!;
      return OutputResult(true, output=result);
    }catch{
      case e: Exception =>
        return OutputResult(false, error=e.getMessage);
    }
  }
}
