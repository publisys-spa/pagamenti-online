/**
 * @license Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	 config.language = 'it';
	// config.uiColor = '#AADC6E';
	
	config.plugins= 'basicstyles,floatingspace,clipboard,list,enterkey,entities,toolbar,undo,wysiwygarea,htmlwriter,maximize,pastefromword,pastetext,selectall,sourcearea,table,wsc';

	config.toolbarGroups = [
	    { name: 'document',    groups: [ 'mode', 'document', 'doctools' ] },
	    { name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
	    { name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
	    { name: 'forms' },
	    { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
	    { name: 'insert'},
	    { name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align', 'bidi'] },
	    { name: 'links' },
	    { name: 'styles' },
	    { name: 'colors' },
	    { name: 'tools' },
	    { name: 'others' }
	];

	//'/',

	// Remove some buttons provided by the standard plugins, which are
	// not needed in the Standard(s) toolbar.
	//config.removeButtons = 'Anchor,Underline,Strike,Subscript,Superscript';
	config.removeButtons = 'Underline,Strike,Subscript,Superscript';

	// Simplify the dialog windows.
	//config.removeDialogTabs = 'image:advanced;link:advanced';

};