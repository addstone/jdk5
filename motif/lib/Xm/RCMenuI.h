/*
 * COPYRIGHT NOTICE
 * Copyright (c) 1990, 1991, 1992, 1993 Open Software Foundation, Inc.
 * ALL RIGHTS RESERVED (MOTIF).  See the file named COPYRIGHT.MOTIF
 * for the full copyright text.
 * 
 */
/*
 * HISTORY
 */
/* $XConsortium: RCMenuI.h /main/5 1995/07/13 17:45:45 drk $ */
#ifndef _XmRCMenuI_h
#define _XmRCMenuI_h

#include <Xm/RowColumn.h>

#ifdef __cplusplus
extern "C" {
#endif

/********    Private Function Declarations    ********/

extern void _XmMenuBtnUp(
        Widget wid,
        XEvent *event,
        String *params,
        Cardinal *num_params);
extern void _XmMenuBtnDown(
        Widget wid,
        XEvent *event,
        String *params,
        Cardinal *num_params);
extern void _XmHandleMenuButtonPress(
				     Widget wid,
				     XEvent *event);
extern Boolean _XmMatchBDragEvent( 
				    Widget wid,
				    XEvent *event);
extern Boolean _XmMatchBSelectEvent( 
				    Widget wid,
				    XEvent *event);
extern void _XmGetActiveTopLevelMenu(
				     Widget wid,
				     Widget *rwid);
extern void _XmMenuFocus(
			 Widget w,
			 int operation,
			 Time _time );
extern void _XmSetSwallowEventHandler(
				      Widget widget, 
#if NeedWidePrototypes
				      int add_handler );
#else
                                      Boolean add_handler );
#endif /* NeedWidePrototypes */
extern void _XmMenuPopDown(
			   Widget w,
			   XEvent *event,
			   Boolean *popped_up );
extern Boolean _XmGetPopupMenuClick(
				    Widget wid );
extern void _XmSetPopupMenuClick(
				 Widget wid,
#if NeedWidePrototypes
				 int popupMenuClick);
#else
                                 Boolean popupMenuClick);
#endif /* NeedWidePrototypes */
extern void _XmRC_DoProcessMenuTree( 
                        Widget w,
                        int mode) ;

extern void _XmRC_ProcessSingleWidget( 
                        Widget w,
                        int mode) ;
extern void _XmRC_AddToPostFromList( 
                        XmRowColumnWidget m,
                        Widget widget) ;
extern void _XmRC_UpdateOptionMenuCBG( 
                        Widget cbg,
                        Widget memWidget) ;
extern void _XmRC_SetMenuHistory( 
                        XmRowColumnWidget m,
                        RectObj child) ;
extern void _XmRC_SetOptionMenuHistory( 
                        XmRowColumnWidget m,
                        RectObj child) ;
extern void _XmRCMenuProcedureEntry(
				    int proc,
				    Widget widget,
				    ... ) ;
extern void _XmRCArmAndActivate(
				Widget w,
				XEvent *event,
				String *parms,
				Cardinal *num_parms );
extern void _XmRCGetTopManager(
			       Widget w,
			       Widget *topManager ) ;
extern void _XmMenuFocusOut( 
                        Widget cb,
                        XEvent *event,
                        String *param,
                        Cardinal *num_param) ;
extern void _XmMenuFocusIn( 
                        Widget wid,
                        XEvent *event,
                        String *param,
                        Cardinal *num_param) ;
extern void _XmGetMenuKidMargins( 
                        XmRowColumnWidget m,
                        Dimension *width,
                        Dimension *height,
                        Dimension *left,
                        Dimension *right,
                        Dimension *top,
                        Dimension *bottom) ;
extern void _XmMenuUnmap( 
                        Widget wid,
                        XEvent *event,
                        String *param,
                        Cardinal *num_param) ;
extern void _XmMenuBarGadgetSelect( 
                        Widget wid,
                        XEvent *event,
                        String *params,
                        Cardinal *num_params) ;
extern void _XmMenuGadgetTraverseCurrent(
                        Widget wid,
                        XEvent *event,
                        String *params,
                        Cardinal *num_params) ;
extern void _XmMenuGadgetTraverseCurrentUp(
                        Widget wid,
                        XEvent *event,
                        String *params,
                        Cardinal *num_params) ;
extern void _XmMenuGadgetDrag(
                        Widget wid,
                        XEvent *event,
                        String *params,
                        Cardinal *num_params) ;
extern int _XmRC_PopupMenuHandler(Widget, XEvent*);

extern Boolean _XmRC_PostTimeOut( XtPointer wid );

extern void _XmRC_RemoveHandlersFromPostFromWidget( 
                        Widget popup,
                        Widget widget) ;
extern void _XmRC_AddPopupEventHandlers( 
                        XmRowColumnWidget pane) ;
extern void _XmRC_RemovePopupEventHandlers( 
                        XmRowColumnWidget pane) ;

void _XmRC_RemoveFromPostFromList( 
                        XmRowColumnWidget m,
                        Widget widget) ;

/********    End Private Function Declarations    ********/


#ifdef __cplusplus
}  /* Close scope of 'extern "C"' declaration which encloses file. */
#endif

#endif /* _XmRCMenuI_h */
/* DON'T ADD ANYTHING AFTER THIS #endif */
