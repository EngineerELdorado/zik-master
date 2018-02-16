<?php 

function setURL()
{
	
	if(!isset($_REQUEST['request']) || empty($_REQUEST['request'])) : 
	      throw new Exception(json_encode(array("message"=>"Set Request")));
     elseif(!function_exists($_REQUEST['request'])) :
		   throw new Exception(json_encode(array("message"=>"Invalid URL")));
		 else : 
	     echo $_REQUEST['request']();
	 endif;
}

   
   try
   {
	  setURL();
   }
   catch(Exception $e)
   {
	   echo $e->getMessage();
   }

?>