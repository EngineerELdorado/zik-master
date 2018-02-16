<?php 

class db 
{   
	public $db;
	function __construct()
	{
    $this->db = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
    }
} 
?>