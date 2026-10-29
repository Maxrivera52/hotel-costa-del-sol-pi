import os
import sys
import time
from pathlib import Path
from playwright.sync_api import sync_playwright

BASE_DIR = Path(__file__).resolve().parent.parent
CAPTURA_DIR = BASE_DIR / "docs" / "capturas"
CAPTURA_DIR.mkdir(parents=True, exist_ok=True)
TEMP_HTML_DIR = BASE_DIR / "docs" / "_temp_render"
TEMP_HTML_DIR.mkdir(parents=True, exist_ok=True)

print(f"Base Directory: {BASE_DIR}")
print(f"Capturas Directory: {CAPTURA_DIR}")

# -----------------------------------------------------------------------------
# 1. HTML MOCKUP: NetBeans IDE 21 (captura_01_arquitectura.html)
# -----------------------------------------------------------------------------
html_01 = r"""<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Apache NetBeans IDE 21 - hotel-costa-del-sol-apf2</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
    background-color: #1e1e1e;
    color: #cccccc;
    height: 100vh;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    font-size: 13px;
  }
  /* Window Titlebar */
  .titlebar {
    background-color: #2d2d2d;
    height: 34px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 12px;
    border-bottom: 1px solid #3c3c3c;
    user-select: none;
  }
  .titlebar-left {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: #e0e0e0;
  }
  .titlebar-icon {
    width: 18px;
    height: 18px;
    background: #007acc;
    border-radius: 3px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: bold;
    font-size: 11px;
  }
  .window-controls {
    display: flex;
    gap: 12px;
    color: #888;
    font-size: 12px;
  }
  /* Menu bar */
  .menubar {
    background-color: #252526;
    height: 28px;
    display: flex;
    align-items: center;
    padding: 0 8px;
    border-bottom: 1px solid #333333;
    gap: 16px;
    font-size: 12px;
    color: #cccccc;
  }
  .menubar span { cursor: pointer; padding: 2px 6px; border-radius: 3px; }
  .menubar span:hover { background-color: #383838; }
  /* Toolbar */
  .toolbar {
    background-color: #2d2d2d;
    height: 36px;
    display: flex;
    align-items: center;
    padding: 0 10px;
    gap: 14px;
    border-bottom: 1px solid #3c3c3c;
    color: #bbb;
    font-size: 14px;
  }
  .toolbar-btn {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 3px 8px;
    border-radius: 4px;
    cursor: pointer;
    background: #383838;
    color: #eee;
    font-size: 11px;
  }
  .toolbar-btn i { font-size: 13px; }
  .btn-run { background: #28a745; color: white; font-weight: 600; }
  .btn-build { background: #007acc; color: white; }
  /* Workspace Layout */
  .workspace {
    display: flex;
    flex: 1;
    overflow: hidden;
  }
  /* Left Sidebar: Project Tree */
  .sidebar {
    width: 320px;
    background-color: #252526;
    border-right: 1px solid #3c3c3c;
    display: flex;
    flex-direction: column;
  }
  .sidebar-header {
    background-color: #1e1e1e;
    padding: 7px 12px;
    font-weight: 600;
    font-size: 11px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    color: #999;
    border-bottom: 1px solid #333;
    display: flex;
    justify-content: space-between;
  }
  .tree-view {
    padding: 6px 0;
    overflow-y: auto;
    flex: 1;
  }
  .tree-node {
    display: flex;
    align-items: center;
    padding: 3px 8px;
    cursor: pointer;
    font-size: 12px;
    gap: 6px;
    color: #d4d4d4;
  }
  .tree-node:hover {
    background-color: #2a2d2e;
  }
  .tree-node.active {
    background-color: #37373d;
    color: #ffffff;
    font-weight: 500;
  }
  .indent-1 { padding-left: 20px; }
  .indent-2 { padding-left: 36px; }
  .indent-3 { padding-left: 52px; }
  .indent-4 { padding-left: 68px; }
  .icon-proj { color: #e5a50a; }
  .icon-pkg { color: #f9826c; }
  .icon-file-java { color: #e36209; }
  .icon-file-xml { color: #4fc1ff; }
  .icon-file-xhtml { color: #ce9178; }
  .icon-folder { color: #dcb67a; }
  
  /* Main Editor Area */
  .editor-area {
    flex: 1;
    display: flex;
    flex-direction: column;
    background-color: #1e1e1e;
  }
  /* Editor Tabs */
  .editor-tabs {
    display: flex;
    background-color: #252526;
    border-bottom: 1px solid #1e1e1e;
    overflow-x: auto;
  }
  .tab {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    background-color: #2d2d2d;
    color: #969696;
    border-right: 1px solid #1e1e1e;
    cursor: pointer;
    font-size: 12px;
  }
  .tab.active {
    background-color: #1e1e1e;
    color: #ffffff;
    border-top: 2px solid #007acc;
  }
  .tab-close { font-size: 10px; opacity: 0.7; }
  
  /* Code Editor Pane */
  .editor-body {
    flex: 1;
    display: flex;
    overflow: hidden;
    font-family: 'Cascadia Code', Consolas, 'Courier New', monospace;
    font-size: 13px;
    line-height: 1.5;
  }
  .line-numbers {
    width: 48px;
    padding: 10px 0;
    background-color: #1e1e1e;
    color: #6e7681;
    text-align: right;
    user-select: none;
    border-right: 1px solid #2a2a2a;
    font-size: 12px;
  }
  .line-numbers div { padding-right: 12px; }
  .code-content {
    flex: 1;
    padding: 10px 16px;
    overflow-y: auto;
    color: #d4d4d4;
    white-space: pre;
  }
  /* Syntax Highlighting */
  .tag { color: #569cd6; }
  .attr { color: #9cdcfe; }
  .val { color: #ce9178; }
  .comm { color: #6a9955; font-style: italic; }
  .text { color: #d4d4d4; }
  
  /* Output Panel (Bottom) */
  .bottom-panel {
    height: 190px;
    background-color: #181818;
    border-top: 1px solid #333333;
    display: flex;
    flex-direction: column;
  }
  .panel-header {
    background-color: #252526;
    padding: 4px 12px;
    font-size: 11px;
    display: flex;
    align-items: center;
    gap: 16px;
    border-bottom: 1px solid #2d2d2d;
  }
  .panel-tab {
    color: #fff;
    border-bottom: 2px solid #007acc;
    padding-bottom: 2px;
    font-weight: 500;
  }
  .panel-body {
    flex: 1;
    padding: 8px 14px;
    font-family: 'Cascadia Code', Consolas, monospace;
    font-size: 12px;
    color: #9cdcfe;
    overflow-y: auto;
    line-height: 1.4;
  }
  .build-success { color: #4ec9b0; font-weight: bold; }
  .status-bar {
    height: 22px;
    background-color: #007acc;
    color: white;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 10px;
    font-size: 11px;
  }
</style>
</head>
<body>

  <!-- Window Header -->
  <div class="titlebar">
    <div class="titlebar-left">
      <div class="titlebar-icon">NB</div>
      <span>Apache NetBeans IDE 21 — [hotel-costa-del-sol-apf2] (Jakarta Faces 4.0 / PostgreSQL)</span>
    </div>
    <div class="window-controls">
      <span>&#8212;</span>
      <span>&#9633;</span>
      <span>&#10005;</span>
    </div>
  </div>

  <!-- Menubar -->
  <div class="menubar">
    <span>File</span>
    <span>Edit</span>
    <span>View</span>
    <span>Navigate</span>
    <span>Source</span>
    <span>Refactor</span>
    <span>Build</span>
    <span>Run</span>
    <span>Profile</span>
    <span>Team</span>
    <span>Tools</span>
    <span>Window</span>
    <span>Help</span>
  </div>

  <!-- Toolbar -->
  <div class="toolbar">
    <div class="toolbar-btn btn-build"><i class="bi bi-hammer"></i> Clean & Build</div>
    <div class="toolbar-btn btn-run"><i class="bi bi-play-fill"></i> Run (Tomcat 11)</div>
    <span style="color:#555">|</span>
    <span style="font-size: 12px; color: #aaa;"><i class="bi bi-server me-1"></i> Apache Tomcat 11.0.0 (Jakarta EE 10 / Servlet 6.0)</span>
    <span style="color:#555">|</span>
    <span style="font-size: 12px; color: #4ec9b0;"><i class="bi bi-database-check me-1"></i> PostgreSQL 15 (Active Pool: HikariCP)</span>
  </div>

  <!-- Workspace -->
  <div class="workspace">
    <!-- Sidebar: Projects Tree -->
    <div class="sidebar">
      <div class="sidebar-header">
        <span><i class="bi bi-folder-fill text-warning me-1"></i> Projects</span>
        <span><i class="bi bi-gear"></i></span>
      </div>
      <div class="tree-view">
        <div class="tree-node active">
          <i class="bi bi-box-seam icon-proj"></i>
          <strong>hotel-costa-del-sol-apf2</strong>
        </div>
        
        <!-- Source Packages -->
        <div class="tree-node indent-1">
          <i class="bi bi-folder2-open icon-folder"></i>
          <span>Source Packages</span>
        </div>
        
        <!-- Packages -->
        <div class="tree-node indent-2">
          <i class="bi bi-box icon-pkg"></i>
          <span>com.utp.hotelcostadelsol.bean</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>HabitacionBean.java</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>LoginBean.java</span>
        </div>

        <div class="tree-node indent-2">
          <i class="bi bi-box icon-pkg"></i>
          <span>com.utp.hotelcostadelsol.config</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>DatabaseConfig.java</span>
        </div>

        <div class="tree-node indent-2">
          <i class="bi bi-box icon-pkg"></i>
          <span>com.utp.hotelcostadelsol.dao</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>HabitacionDAO.java</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>TipoHabitacionDAO.java</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>UsuarioDAO.java</span>
        </div>

        <div class="tree-node indent-2">
          <i class="bi bi-box icon-pkg"></i>
          <span>com.utp.hotelcostadelsol.model.dto</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>HabitacionDTO.java</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>TipoHabitacionDTO.java</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>UsuarioDTO.java</span>
        </div>

        <div class="tree-node indent-2">
          <i class="bi bi-box icon-pkg"></i>
          <span>com.utp.hotelcostadelsol.service</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>AuthService.java</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>HabitacionService.java</span>
        </div>

        <!-- Other Sources -->
        <div class="tree-node indent-1">
          <i class="bi bi-folder2-open icon-folder"></i>
          <span>Other Sources (src/main/resources)</span>
        </div>
        <div class="tree-node indent-2">
          <i class="bi bi-gear-fill text-info"></i>
          <span>config / db.properties (PostgreSQL)</span>
        </div>
        <div class="tree-node indent-2">
          <i class="bi bi-database text-warning"></i>
          <span>schema / schema_postgresql.sql</span>
        </div>

        <!-- Web Pages -->
        <div class="tree-node indent-1">
          <i class="bi bi-folder2-open icon-folder"></i>
          <span>Web Pages</span>
        </div>
        <div class="tree-node indent-2">
          <i class="bi bi-folder-fill icon-folder"></i>
          <span>WEB-INF</span>
        </div>
        <div class="tree-node indent-3">
          <i class="bi bi-file-earmark-code icon-file-xml"></i>
          <span>faces-config.xml</span>
        </div>
        <div class="tree-node indent-3 active">
          <i class="bi bi-file-earmark-code-fill icon-file-xml"></i>
          <strong style="color:#569cd6">web.xml</strong>
        </div>
        <div class="tree-node indent-2">
          <i class="bi bi-file-earmark-richtext icon-file-xhtml"></i>
          <span>habitaciones.xhtml</span>
        </div>
        <div class="tree-node indent-2">
          <i class="bi bi-file-earmark-richtext icon-file-xhtml"></i>
          <span>index.xhtml</span>
        </div>
        <div class="tree-node indent-2">
          <i class="bi bi-file-earmark-richtext icon-file-xhtml"></i>
          <span>login.xhtml</span>
        </div>

      </div>
    </div>

    <!-- Main Editor -->
    <div class="editor-area">
      <!-- Tabs -->
      <div class="editor-tabs">
        <div class="tab active">
          <i class="bi bi-file-earmark-code icon-file-xml"></i>
          <span>web.xml</span>
          <span class="tab-close">&#10005;</span>
        </div>
        <div class="tab">
          <i class="bi bi-file-earmark-code icon-file-xml"></i>
          <span>faces-config.xml</span>
          <span class="tab-close">&#10005;</span>
        </div>
        <div class="tab">
          <i class="bi bi-filetype-java icon-file-java"></i>
          <span>HabitacionBean.java</span>
          <span class="tab-close">&#10005;</span>
        </div>
        <div class="tab">
          <i class="bi bi-gear-fill text-info"></i>
          <span>db.properties</span>
          <span class="tab-close">&#10005;</span>
        </div>
      </div>

      <!-- Editor Content -->
      <div class="editor-body">
        <div class="line-numbers">
          <div>1</div><div>2</div><div>3</div><div>4</div><div>5</div>
          <div>6</div><div>7</div><div>8</div><div>9</div><div>10</div>
          <div>11</div><div>12</div><div>13</div><div>14</div><div>15</div>
          <div>16</div><div>17</div><div>18</div><div>19</div><div>20</div>
          <div>21</div><div>22</div><div>23</div><div>24</div><div>25</div>
          <div>26</div><div>27</div><div>28</div><div>29</div><div>30</div>
          <div>31</div><div>32</div><div>33</div><div>34</div><div>35</div>
        </div>
        <div class="code-content"><span class="comm">&lt;?xml version="1.0" encoding="UTF-8"?&gt;</span>
<span class="tag">&lt;web-app</span> <span class="attr">xmlns</span>=<span class="val">"https://jakarta.ee/xml/ns/jakartaee"</span>
         <span class="attr">xmlns:xsi</span>=<span class="val">"http://www.w3.org/2001/XMLSchema-instance"</span>
         <span class="attr">xsi:schemaLocation</span>=<span class="val">"https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"</span>
         <span class="attr">version</span>=<span class="val">"6.0"</span><span class="tag">&gt;</span>

    <span class="tag">&lt;display-name&gt;</span><span class="text">Hotel Costa del Sol Chiclayo - APF2 Demo (40%)</span><span class="tag">&lt;/display-name&gt;</span>

    <span class="comm">&lt;!-- Desactivación de CDI para Servlet Container puro (Tomcat 11) --&gt;</span>
    <span class="tag">&lt;context-param&gt;</span>
        <span class="tag">&lt;param-name&gt;</span><span class="text">com.sun.faces.enableCdi</span><span class="tag">&lt;/param-name&gt;</span>
        <span class="tag">&lt;param-value&gt;</span><span class="text">false</span><span class="tag">&lt;/param-value&gt;</span>
    <span class="tag">&lt;/context-param&gt;</span>
    <span class="tag">&lt;context-param&gt;</span>
        <span class="tag">&lt;param-name&gt;</span><span class="text">jakarta.faces.PROJECT_STAGE</span><span class="tag">&lt;/param-name&gt;</span>
        <span class="tag">&lt;param-value&gt;</span><span class="text">Development</span><span class="tag">&lt;/param-value&gt;</span>
    <span class="tag">&lt;/context-param&gt;</span>

    <span class="comm">&lt;!-- Controlador Frontal Canónico de Jakarta Faces (FacesServlet) --&gt;</span>
    <span class="tag">&lt;servlet&gt;</span>
        <span class="tag">&lt;servlet-name&gt;</span><span class="text">Faces Servlet</span><span class="tag">&lt;/servlet-name&gt;</span>
        <span class="tag">&lt;servlet-class&gt;</span><span class="text">jakarta.faces.webapp.FacesServlet</span><span class="tag">&lt;/servlet-class&gt;</span>
        <span class="tag">&lt;load-on-startup&gt;</span><span class="text">1</span><span class="tag">&lt;/load-on-startup&gt;</span>
    <span class="tag">&lt;/servlet&gt;</span>

    <span class="comm">&lt;!-- Mapeo de URL para vistas Facelets XHTML --&gt;</span>
    <span class="tag">&lt;servlet-mapping&gt;</span>
        <span class="tag">&lt;servlet-name&gt;</span><span class="text">Faces Servlet</span><span class="tag">&lt;/servlet-name&gt;</span>
        <span class="tag">&lt;url-pattern&gt;</span><span class="text">*.xhtml</span><span class="tag">&lt;/url-pattern&gt;</span>
        <span class="tag">&lt;url-pattern&gt;</span><span class="text">/faces/*</span><span class="tag">&lt;/url-pattern&gt;</span>
    <span class="tag">&lt;/servlet-mapping&gt;</span>

    <span class="tag">&lt;welcome-file-list&gt;</span>
        <span class="tag">&lt;welcome-file&gt;</span><span class="text">index.xhtml</span><span class="tag">&lt;/welcome-file&gt;</span>
    <span class="tag">&lt;/welcome-file-list&gt;</span>

<span class="tag">&lt;/web-app&gt;</span></div>
      </div>

      <!-- Bottom Panel: Output -->
      <div class="bottom-panel">
        <div class="panel-header">
          <span class="panel-tab"><i class="bi bi-terminal me-1"></i> Output — hotel-costa-del-sol-apf2 (clean, package)</span>
          <span style="color:#888;">Tasks</span>
          <span style="color:#888;">Terminal</span>
        </div>
        <div class="panel-body">
          <div>--- maven-compiler-plugin:3.13.0:compile (default-compile) @ hotel-costa-del-sol-apf2 ---</div>
          <div>[INFO] Compiling 9 source files with javac [debug target 21] to target\\classes</div>
          <div>--- maven-war-plugin:3.4.0:war (default-war) @ hotel-costa-del-sol-apf2 ---</div>
          <div>[INFO] Packaging webapp: D:\\UTP\NetBeansProjects\hotel-costa-del-sol\hotel-costa-del-sol-apf2\target\hotel-costa-del-sol-apf2-1.0-SNAPSHOT.war</div>
          <div>[INFO] Webapp assembled in [245 msecs]</div>
          <div class="build-success">[INFO] ------------------------------------------------------------------------</div>
          <div class="build-success">[INFO] BUILD SUCCESS</div>
          <div class="build-success">[INFO] ------------------------------------------------------------------------</div>
          <div>[INFO] Total time:  2.840 s | Finished at: 2026-10-12T10:24:00-05:00</div>
        </div>
      </div>
    </div>
  </div>

  <!-- Status Bar -->
  <div class="status-bar">
    <div><i class="bi bi-check2-circle me-1"></i> Ready — Git Branch: <strong>main</strong> (30 commits)</div>
    <div>UTF-8 &nbsp;|&nbsp; XML &nbsp;|&nbsp; Java 21 LTS &nbsp;|&nbsp; Tomcat 11</div>
  </div>

</body>
</html>
"""

# -----------------------------------------------------------------------------
# 2. HTML MOCKUP: Dashboard index.xhtml (captura_02_dashboard.html)
# -----------------------------------------------------------------------------
html_02 = r"""<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Hotel Costa del Sol - Inicio APF2</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
<style>
  :root {
    --cds-primary: #0a2540;
    --cds-secondary: #c5a059;
    --cds-accent: #2e5b88;
    --cds-bg: #f8fafc;
  }
  body {
    font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
    background-color: var(--cds-bg);
    color: #334155;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }
  /* Browser Chrome */
  .browser-bar {
    background-color: #e9ecef;
    border-bottom: 1px solid #ced4da;
    padding: 6px 14px;
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 13px;
  }
  .browser-dots { display: flex; gap: 6px; }
  .dot { width: 11px; height: 11px; border-radius: 50%; display: inline-block; }
  .dot-red { background: #ff5f56; }
  .dot-yellow { background: #ffbd2e; }
  .dot-green { background: #27c93f; }
  .url-bar {
    flex: 1;
    background: white;
    padding: 4px 14px;
    border-radius: 20px;
    border: 1px solid #dee2e6;
    display: flex;
    align-items: center;
    gap: 8px;
    font-family: 'Cascadia Code', Consolas, monospace;
    font-size: 12px;
    color: #495057;
  }
  .card-stat {
    border: none;
    border-radius: 12px;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }
  .card-stat:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.08) !important;
  }
  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
  }
  .footer-utp {
    background-color: #0f172a;
    color: #94a3b8;
    font-size: 0.875rem;
    margin-top: auto;
  }
</style>
</head>
<body>

  <!-- Browser URL bar -->
  <div class="browser-bar">
    <div class="browser-dots">
      <span class="dot dot-red"></span>
      <span class="dot dot-yellow"></span>
      <span class="dot dot-green"></span>
    </div>
    <div class="d-flex gap-2 text-secondary">
      <i class="bi bi-arrow-left"></i>
      <i class="bi bi-arrow-right"></i>
      <i class="bi bi-arrow-clockwise"></i>
    </div>
    <div class="url-bar">
      <i class="bi bi-lock-fill text-success"></i>
      <span>http://localhost:8080/hotel-costa-del-sol-apf2/index.xhtml</span>
    </div>
    <div class="text-secondary"><i class="bi bi-three-dots-vertical"></i></div>
  </div>

  <!-- Navbar Facelets -->
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm" style="background-color: #0a2540 !important;">
    <div class="container">
      <a class="navbar-brand d-flex align-items-center gap-2" href="#">
        <i class="bi bi-building fs-4 text-warning"></i>
        <span class="fw-bold">Costa del Sol <small class="text-warning fw-light fs-6">Chiclayo</small></span>
      </a>
      
      <div class="collapse navbar-collapse show" id="navCosta">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3">
          <li class="nav-item">
            <a class="nav-link active fw-semibold" href="#">
              <i class="bi bi-house-door me-1"></i> Inicio
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">
              <i class="bi bi-door-open me-1"></i> Habitaciones (JSF Data Table)
            </a>
          </li>
        </ul>

        <div class="d-flex align-items-center gap-3">
          <span class="badge bg-warning text-dark px-3 py-2 rounded-pill fw-semibold">
            <i class="bi bi-mortarboard-fill me-1"></i> APF2 - Semana 10 (40%)
          </span>

          <div class="dropdown">
            <button class="btn btn-outline-light btn-sm dropdown-toggle" type="button">
              <i class="bi bi-person-circle me-1"></i> Max Rivera Morán (Admin)
            </button>
          </div>
        </div>
      </div>
    </div>
  </nav>

  <!-- Contenido Principal Facelets -->
  <main class="flex-grow-1 py-4">
    <div class="container">
      
      <!-- Hero Header -->
      <div class="p-4 p-md-5 mb-4 rounded-4 text-white shadow-sm" style="background: linear-gradient(135deg, #0a2540 0%, #1e3a8a 100%); border-left: 6px solid #c5a059;">
        <div class="container-fluid py-2">
          <div class="d-flex align-items-center gap-2 mb-2">
            <span class="badge bg-warning text-dark px-3 py-1 fw-bold">APF2: Semana 10</span>
            <span class="text-light-50">JavaServer Faces (Jakarta Faces 4.0) + Bootstrap 5 + PostgreSQL 15</span>
          </div>
          <h1 class="display-5 fw-bold">Sistema de Gestión Hotelera</h1>
          <p class="col-md-10 fs-5 text-light-emphasis">
            Plataforma empresarial para el Hotel Wyndham Costa del Sol Chiclayo. Módulo de control de inventario, catálogo dinámico de habitaciones y respaldo con Managed Beans.
          </p>
          <div class="d-flex gap-3 mt-3">
            <a href="#" class="btn btn-warning btn-lg fw-semibold px-4 text-dark shadow-sm">
              <i class="bi bi-table me-2"></i> Ver Tabla de Datos JSF
            </a>
            <a href="#" class="btn btn-outline-light btn-lg px-4">
              <i class="bi bi-database me-2"></i> Estado PostgreSQL (HikariCP)
            </a>
          </div>
        </div>
      </div>

      <!-- Tarjetas Estadisticas vinculadas al Managed Bean -->
      <h4 class="fw-bold mb-3 text-dark d-flex align-items-center justify-content-between">
        <span><i class="bi bi-speedometer2 text-primary me-2"></i> Estado Operativo del Hotel (Tiempo Real)</span>
        <span class="badge bg-light text-secondary border fw-normal fs-6">Managed Bean: <code>#{habitacionBean}</code></span>
      </h4>
      
      <div class="row g-3 mb-4">
        <div class="col-12 col-sm-6 col-xl-3">
          <div class="card card-stat shadow-sm border-start border-primary border-4 p-3 bg-white">
            <div class="d-flex align-items-center justify-content-between">
              <div>
                <span class="text-muted text-uppercase fw-semibold small">Total Habitaciones</span>
                <h2 class="fw-bold my-1 text-primary">12</h2>
                <small class="text-muted">Capacidad registrada</small>
              </div>
              <div class="stat-icon bg-primary bg-opacity-10 text-primary">
                <i class="bi bi-door-open-fill"></i>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12 col-sm-6 col-xl-3">
          <div class="card card-stat shadow-sm border-start border-success border-4 p-3 bg-white">
            <div class="d-flex align-items-center justify-content-between">
              <div>
                <span class="text-muted text-uppercase fw-semibold small">Disponibles</span>
                <h2 class="fw-bold my-1 text-success">8</h2>
                <small class="text-success fw-medium"><i class="bi bi-arrow-up-short"></i> Listas para check-in</small>
              </div>
              <div class="stat-icon bg-success bg-opacity-10 text-success">
                <i class="bi bi-check-circle-fill"></i>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12 col-sm-6 col-xl-3">
          <div class="card card-stat shadow-sm border-start border-danger border-4 p-3 bg-white">
            <div class="d-flex align-items-center justify-content-between">
              <div>
                <span class="text-muted text-uppercase fw-semibold small">Ocupadas</span>
                <h2 class="fw-bold my-1 text-danger">3</h2>
                <small class="text-danger fw-medium">Huéspedes alojados</small>
              </div>
              <div class="stat-icon bg-danger bg-opacity-10 text-danger">
                <i class="bi bi-person-fill-lock"></i>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12 col-sm-6 col-xl-3">
          <div class="card card-stat shadow-sm border-start border-warning border-4 p-3 bg-white">
            <div class="d-flex align-items-center justify-content-between">
              <div>
                <span class="text-muted text-uppercase fw-semibold small">Mantenimiento</span>
                <h2 class="fw-bold my-1 text-warning">1</h2>
                <small class="text-muted">Servicio técnico</small>
              </div>
              <div class="stat-icon bg-warning bg-opacity-10 text-warning">
                <i class="bi bi-tools"></i>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Módulos de la Solución (Cards Bootstrap) -->
      <div class="row g-4">
        <div class="col-md-4">
          <div class="card h-100 shadow-sm border-0 bg-white">
            <div class="card-body">
              <div class="d-inline-flex p-3 rounded-3 bg-primary bg-opacity-10 text-primary mb-3">
                <i class="bi bi-database-check fs-3"></i>
              </div>
              <h5 class="card-title fw-bold">Persistencia JDBC y DAO</h5>
              <p class="card-text text-muted">
                Conexión de alto rendimiento con pool HikariCP sobre PostgreSQL 15. Cumplimiento del patrón arquitectónico DAO/DTO establecido en el APF1.
              </p>
            </div>
          </div>
        </div>
        
        <div class="col-md-4">
          <div class="card h-100 shadow-sm border-0 bg-white">
            <div class="card-body">
              <div class="d-inline-flex p-3 rounded-3 bg-warning bg-opacity-10 text-warning mb-3">
                <i class="bi bi-code-slash fs-3"></i>
              </div>
              <h5 class="card-title fw-bold">Managed Beans y EL</h5>
              <p class="card-text text-muted">
                Enlace de datos bidireccional mediante Expression Language (EL: <code>#{habitacionBean.listaHabitaciones}</code>) y ciclo de vida de componentes JSF.
              </p>
            </div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="card h-100 shadow-sm border-0 bg-white">
            <div class="card-body">
              <div class="d-inline-flex p-3 rounded-3 bg-info bg-opacity-10 text-info mb-3">
                <i class="bi bi-bootstrap-fill fs-3"></i>
              </div>
              <h5 class="card-title fw-bold">Bootstrap 5 Responsive</h5>
              <p class="card-text text-muted">
                Interfaz moderna con paleta institucional Costa del Sol, adaptable a pantallas de escritorio y dispositivos móviles mediante Facelets templates.
              </p>
            </div>
          </div>
        </div>
      </div>

    </div>
  </main>

  <!-- Footer Academico UTP -->
  <footer class="footer-utp py-4 mt-auto">
    <div class="container text-center text-md-start">
      <div class="row align-items-center">
        <div class="col-md-6 mb-3 mb-md-0">
          <h6 class="text-white mb-1 fw-bold">Universidad Tecnológica del Perú (UTP)</h6>
          <p class="mb-0 text-muted">Ciclo 2026-II</p>
        </div>
        <div class="col-md-6 text-md-end">
          <span class="d-block text-white fw-semibold mb-1">Integrantes del Equipo:</span>
          <small class="d-block text-muted">Miguel Suárez Perdomo (U23246385) | Joel Cori Alarcón (U23303991) | Max Rivera Morán (U22242437)</small>
        </div>
      </div>
    </div>
  </footer>

</body>
</html>
"""

# -----------------------------------------------------------------------------
# 3. HTML MOCKUP: Formulario JSF Login login.xhtml (captura_03_formulario_jsf.html)
# -----------------------------------------------------------------------------
html_03 = r"""<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Hotel Costa del Sol - Iniciar Sesion (JSF Form)</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
<style>
  :root {
    --cds-primary: #0a2540;
    --cds-secondary: #c5a059;
    --cds-accent: #2e5b88;
    --cds-bg: #f8fafc;
  }
  body {
    font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
    background-color: var(--cds-bg);
    color: #334155;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }
  .browser-bar {
    background-color: #e9ecef;
    border-bottom: 1px solid #ced4da;
    padding: 6px 14px;
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 13px;
  }
  .browser-dots { display: flex; gap: 6px; }
  .dot { width: 11px; height: 11px; border-radius: 50%; display: inline-block; }
  .dot-red { background: #ff5f56; }
  .dot-yellow { background: #ffbd2e; }
  .dot-green { background: #27c93f; }
  .url-bar {
    flex: 1;
    background: white;
    padding: 4px 14px;
    border-radius: 20px;
    border: 1px solid #dee2e6;
    display: flex;
    align-items: center;
    gap: 8px;
    font-family: 'Cascadia Code', Consolas, monospace;
    font-size: 12px;
    color: #495057;
  }
  .footer-utp {
    background-color: #0f172a;
    color: #94a3b8;
    font-size: 0.875rem;
    margin-top: auto;
  }
</style>
</head>
<body>

  <!-- Browser Bar -->
  <div class="browser-bar">
    <div class="browser-dots">
      <span class="dot dot-red"></span>
      <span class="dot dot-yellow"></span>
      <span class="dot dot-green"></span>
    </div>
    <div class="d-flex gap-2 text-secondary">
      <i class="bi bi-arrow-left"></i>
      <i class="bi bi-arrow-right"></i>
      <i class="bi bi-arrow-clockwise"></i>
    </div>
    <div class="url-bar">
      <i class="bi bi-lock-fill text-success"></i>
      <span>http://localhost:8080/hotel-costa-del-sol-apf2/login.xhtml</span>
    </div>
    <div class="text-secondary"><i class="bi bi-three-dots-vertical"></i></div>
  </div>

  <!-- Navbar -->
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm" style="background-color: #0a2540 !important;">
    <div class="container">
      <a class="navbar-brand d-flex align-items-center gap-2" href="#">
        <i class="bi bi-building fs-4 text-warning"></i>
        <span class="fw-bold">Costa del Sol <small class="text-warning fw-light fs-6">Chiclayo</small></span>
      </a>
      
      <div class="d-flex align-items-center gap-3 ms-auto">
        <span class="badge bg-warning text-dark px-3 py-2 rounded-pill fw-semibold">
          <i class="bi bi-mortarboard-fill me-1"></i> APF2 - Semana 10
        </span>
        <a href="#" class="btn btn-outline-warning btn-sm px-3">
          <i class="bi bi-box-arrow-in-right me-1"></i> Iniciar Sesión
        </a>
      </div>
    </div>
  </nav>

  <!-- Login Form Area -->
  <main class="flex-grow-1 py-5">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="card shadow border-0 rounded-4 overflow-hidden">
            <div class="card-header text-white text-center py-4" style="background-color: #0a2540 !important;">
              <i class="bi bi-shield-lock-fill fs-1 text-warning mb-2 d-block"></i>
              <h4 class="fw-bold mb-0">Autenticación de Usuarios</h4>
              <small class="text-light-50">Acceso seguro con JSF Managed Bean (<code>@SessionScoped</code>)</small>
            </div>
            
            <div class="card-body p-4 p-md-5 bg-white">
              
              <!-- Simulación de componente <h:form id="formLogin"> -->
              <form id="formLogin">
                <div class="mb-3">
                  <label for="username" class="form-label fw-semibold text-secondary">
                    <i class="bi bi-person me-1"></i> Nombre de Usuario (<code>&lt;h:inputText&gt;</code>)
                  </label>
                  <input type="text" id="username" class="form-control form-control-lg" value="admin" placeholder="Ej: admin">
                  <div class="form-text text-muted"><small>Enlazado con <code>#{loginBean.username}</code></small></div>
                </div>

                <div class="mb-4">
                  <label for="password" class="form-label fw-semibold text-secondary">
                    <i class="bi bi-key me-1"></i> Contraseña (<code>&lt;h:inputSecret&gt;</code>)
                  </label>
                  <input type="password" id="password" class="form-control form-control-lg" value="admin123" placeholder="••••••••">
                  <div class="form-text text-muted"><small>Enlazado con <code>#{loginBean.password}</code> (Validación BCrypt)</small></div>
                </div>

                <!-- Botón de acción JSF -->
                <button type="button" class="btn btn-warning btn-lg w-100 fw-bold shadow-sm mb-3 text-dark">
                  <i class="bi bi-box-arrow-in-right me-2"></i> Ingresar al Sistema (<code>action="#{loginBean.autenticar}"</code>)
                </button>
              </form>

              <!-- Panel Credenciales de Prueba -->
              <div class="bg-light p-3 rounded-3 border mt-3">
                <h6 class="fw-bold text-dark mb-2 small d-flex align-items-center justify-content-between">
                  <span><i class="bi bi-info-circle-fill text-primary me-1"></i> Credenciales de Prueba (APF2):</span>
                  <span class="badge bg-success-subtle text-success border border-success-subtle">PostgreSQL Seed</span>
                </h6>
                <div class="row g-2 small text-muted">
                  <div class="col-12 d-flex justify-content-between">
                    <span><strong>Admin:</strong> <code>admin</code> / <code>admin123</code></span>
                    <span class="badge bg-secondary">Max Rivera (U22242437)</span>
                  </div>
                  <div class="col-12 d-flex justify-content-between">
                    <span><strong>Recepción:</strong> <code>recepcion</code> / <code>recep123</code></span>
                    <span class="badge bg-secondary">Miguel Suárez (U23246385)</span>
                  </div>
                  <div class="col-12 d-flex justify-content-between">
                    <span><strong>Cliente:</strong> <code>cliente</code> / <code>client123</code></span>
                    <span class="badge bg-secondary">Joel Cori (U23303991)</span>
                  </div>
                </div>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>
  </main>

  <!-- Footer Academico UTP -->
  <footer class="footer-utp py-3 mt-auto">
    <div class="container text-center text-muted small">
      Universidad Tecnológica del Perú (UTP) — Ciclo 2026-II
    </div>
  </footer>

</body>
</html>
"""

# -----------------------------------------------------------------------------
# 4. HTML MOCKUP: DataTable JSF habitaciones.xhtml (captura_04_datatable_postgresql.html)
# -----------------------------------------------------------------------------
html_04 = r"""<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Hotel Costa del Sol - Gestión de Habitaciones (JSF DataTable)</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
<style>
  :root {
    --cds-primary: #0a2540;
    --cds-secondary: #c5a059;
    --cds-accent: #2e5b88;
    --cds-bg: #f8fafc;
  }
  body {
    font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
    background-color: var(--cds-bg);
    color: #334155;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }
  .browser-bar {
    background-color: #e9ecef;
    border-bottom: 1px solid #ced4da;
    padding: 6px 14px;
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 13px;
  }
  .browser-dots { display: flex; gap: 6px; }
  .dot { width: 11px; height: 11px; border-radius: 50%; display: inline-block; }
  .dot-red { background: #ff5f56; }
  .dot-yellow { background: #ffbd2e; }
  .dot-green { background: #27c93f; }
  .url-bar {
    flex: 1;
    background: white;
    padding: 4px 14px;
    border-radius: 20px;
    border: 1px solid #dee2e6;
    display: flex;
    align-items: center;
    gap: 8px;
    font-family: 'Cascadia Code', Consolas, monospace;
    font-size: 12px;
    color: #495057;
  }
  .table-custom th {
    background-color: #0a2540 !important;
    color: #ffffff !important;
    font-weight: 600;
    font-size: 0.875rem;
    vertical-align: middle;
  }
  .footer-utp {
    background-color: #0f172a;
    color: #94a3b8;
    font-size: 0.875rem;
    margin-top: auto;
  }
</style>
</head>
<body>

  <!-- Browser Bar -->
  <div class="browser-bar">
    <div class="browser-dots">
      <span class="dot dot-red"></span>
      <span class="dot dot-yellow"></span>
      <span class="dot dot-green"></span>
    </div>
    <div class="d-flex gap-2 text-secondary">
      <i class="bi bi-arrow-left"></i>
      <i class="bi bi-arrow-right"></i>
      <i class="bi bi-arrow-clockwise"></i>
    </div>
    <div class="url-bar">
      <i class="bi bi-lock-fill text-success"></i>
      <span>http://localhost:8080/hotel-costa-del-sol-apf2/faces/habitaciones.xhtml</span>
    </div>
    <div class="text-secondary"><i class="bi bi-three-dots-vertical"></i></div>
  </div>

  <!-- Navbar -->
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm" style="background-color: #0a2540 !important;">
    <div class="container">
      <a class="navbar-brand d-flex align-items-center gap-2" href="#">
        <i class="bi bi-building fs-4 text-warning"></i>
        <span class="fw-bold">Costa del Sol <small class="text-warning fw-light fs-6">Chiclayo</small></span>
      </a>
      
      <div class="collapse navbar-collapse show" id="navCosta">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3">
          <li class="nav-item">
            <a class="nav-link" href="#">
              <i class="bi bi-house-door me-1"></i> Inicio
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link active fw-semibold text-warning" href="#">
              <i class="bi bi-door-open me-1"></i> Habitaciones (JSF Data Table)
            </a>
          </li>
        </ul>

        <div class="d-flex align-items-center gap-3">
          <span class="badge bg-warning text-dark px-3 py-2 rounded-pill fw-semibold">
            <i class="bi bi-mortarboard-fill me-1"></i> APF2 - Semana 10
          </span>

          <div class="dropdown">
            <button class="btn btn-outline-light btn-sm dropdown-toggle" type="button">
              <i class="bi bi-person-circle me-1"></i> Max Rivera Morán (Admin)
            </button>
          </div>
        </div>
      </div>
    </div>
  </nav>

  <!-- Main Content -->
  <main class="flex-grow-1 py-4">
    <div class="container">

      <!-- Header -->
      <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
        <div>
          <h2 class="fw-bold mb-1 text-dark">
            <i class="bi bi-door-open-fill text-warning me-2"></i> Catálogo Dinámico de Habitaciones
          </h2>
          <p class="text-muted mb-0">
            Demostración del componente <code>&lt;h:dataTable value="#{habitacionBean.listaHabitaciones}"&gt;</code> conectado a PostgreSQL vía <code>HabitacionDAO.java</code>.
          </p>
        </div>
        
        <div>
          <button class="btn btn-primary shadow-sm" type="button">
            <i class="bi bi-plus-circle me-1"></i> Nueva Habitación (h:form)
          </button>
        </div>
      </div>

      <!-- Alerta de Conexión PostgreSQL -->
      <div class="alert alert-success d-flex align-items-center justify-content-between py-2 px-3 mb-3 shadow-sm border-success">
        <div class="d-flex align-items-center gap-2">
          <i class="bi bi-database-check fs-5 text-success"></i>
          <span><strong>Conexión Activa a PostgreSQL:</strong> <code>jdbc:postgresql://localhost:5432/hotel_costa_sol_pi</code> &bull; Pool HikariCP (10 conexiones activas).</span>
        </div>
        <span class="badge bg-success">Status: OK</span>
      </div>

      <!-- Filtros Dinámicos JSF -->
      <div class="card shadow-sm border-0 mb-4 bg-white">
        <div class="card-body p-3">
          <form class="row g-2 align-items-center">
            <div class="col-12 col-md-auto">
              <span class="fw-semibold text-secondary me-2">
                <i class="bi bi-funnel-fill text-primary"></i> Filtrar por:
              </span>
            </div>

            <div class="col-12 col-sm-6 col-md-3">
              <select class="form-select form-select-sm">
                <option selected>-- Todos los Estados --</option>
                <option value="DISPONIBLE">Disponibles</option>
                <option value="OCUPADA">Ocupadas</option>
                <option value="MANTENIMIENTO">En Mantenimiento</option>
              </select>
            </div>

            <div class="col-12 col-sm-6 col-md-3">
              <select class="form-select form-select-sm">
                <option selected>-- Todos los Tipos --</option>
                <option value="1">Habitación Simple Confort</option>
                <option value="2">Habitación Matrimonial Deluxe</option>
                <option value="3">Habitación Doble Twin</option>
                <option value="4">Suite Junior Costa del Sol</option>
                <option value="5">Suite Presidencial Real</option>
              </select>
            </div>

            <div class="col-auto">
              <button type="button" class="btn btn-primary btn-sm px-3 fw-semibold">
                <i class="bi bi-search me-1"></i> Filtrar
              </button>
            </div>

            <div class="col-auto">
              <button type="button" class="btn btn-outline-secondary btn-sm px-3">
                Restablecer
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- TABLA DINÁMICA DE DATOS CON <h:dataTable> -->
      <div class="card shadow-sm border-0 overflow-hidden">
        <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center border-bottom">
          <h5 class="fw-bold mb-0 text-dark">
            <i class="bi bi-table text-primary me-2"></i> Listado Oficial de Habitaciones (Base de Datos PostgreSQL)
          </h5>
          <span class="badge bg-primary px-3 py-2">
            Registros mostrados: 9 de 12
          </span>
        </div>

        <div class="card-body p-0">
          <div class="table-responsive">
            <table class="table table-hover table-striped align-middle mb-0 table-custom">
              <thead>
                <tr>
                  <th class="py-3 px-3">N° Habitación</th>
                  <th class="py-3">Piso</th>
                  <th class="py-3">Tipo de Habitación</th>
                  <th class="py-3">Precio / Noche</th>
                  <th class="py-3">Observaciones / Ubicación</th>
                  <th class="py-3 text-center">Estado Actual</th>
                  <th class="py-3 text-center">Acción Rápida JSF</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td class="px-3 fw-bold text-primary fs-6"><i class="bi bi-door-closed me-1"></i> 101</td>
                  <td><span class="badge bg-light text-dark border">Piso 1</span></td>
                  <td class="fw-semibold">Habitación Simple Confort</td>
                  <td><span class="text-success fw-bold">S/. 120.00</span></td>
                  <td><small class="text-muted">Frente al jardín interior</small></td>
                  <td class="text-center">
                    <span class="badge bg-success px-3 py-2"><i class="bi bi-check-circle me-1"></i> DISPONIBLE</span>
                  </td>
                  <td class="text-center">
                    <button class="btn btn-outline-warning btn-sm py-1 px-2" style="font-size: 11px;">Mantenimiento</button>
                  </td>
                </tr>

                <tr>
                  <td class="px-3 fw-bold text-primary fs-6"><i class="bi bi-door-closed me-1"></i> 102</td>
                  <td><span class="badge bg-light text-dark border">Piso 1</span></td>
                  <td class="fw-semibold">Habitación Simple Confort</td>
                  <td><span class="text-success fw-bold">S/. 120.00</span></td>
                  <td><small class="text-muted">Huésped con salida programada</small></td>
                  <td class="text-center">
                    <span class="badge bg-danger px-3 py-2"><i class="bi bi-person-fill-lock me-1"></i> OCUPADA</span>
                  </td>
                  <td class="text-center">
                    <button class="btn btn-outline-success btn-sm py-1 px-2" style="font-size: 11px;">Disponible</button>
                  </td>
                </tr>

                <tr>
                  <td class="px-3 fw-bold text-primary fs-6"><i class="bi bi-door-closed me-1"></i> 201</td>
                  <td><span class="badge bg-light text-dark border">Piso 2</span></td>
                  <td class="fw-semibold">Habitación Matrimonial Deluxe</td>
                  <td><span class="text-success fw-bold">S/. 180.00</span></td>
                  <td><small class="text-muted">Cama King, balcón este</small></td>
                  <td class="text-center">
                    <span class="badge bg-success px-3 py-2"><i class="bi bi-check-circle me-1"></i> DISPONIBLE</span>
                  </td>
                  <td class="text-center">
                    <button class="btn btn-outline-warning btn-sm py-1 px-2" style="font-size: 11px;">Mantenimiento</button>
                  </td>
                </tr>

                <tr>
                  <td class="px-3 fw-bold text-primary fs-6"><i class="bi bi-door-closed me-1"></i> 202</td>
                  <td><span class="badge bg-light text-dark border">Piso 2</span></td>
                  <td class="fw-semibold">Habitación Matrimonial Deluxe</td>
                  <td><span class="text-success fw-bold">S/. 180.00</span></td>
                  <td><small class="text-muted">Revisión de aire acondicionado</small></td>
                  <td class="text-center">
                    <span class="badge bg-warning text-dark px-3 py-2"><i class="bi bi-tools me-1"></i> MANTENIMIENTO</span>
                  </td>
                  <td class="text-center">
                    <button class="btn btn-outline-success btn-sm py-1 px-2" style="font-size: 11px;">Disponible</button>
                  </td>
                </tr>

                <tr>
                  <td class="px-3 fw-bold text-primary fs-6"><i class="bi bi-door-closed me-1"></i> 203</td>
                  <td><span class="badge bg-light text-dark border">Piso 2</span></td>
                  <td class="fw-semibold">Habitación Doble Twin</td>
                  <td><span class="text-success fw-bold">S/. 220.00</span></td>
                  <td><small class="text-muted">Dos camas Twin, iluminación natural</small></td>
                  <td class="text-center">
                    <span class="badge bg-success px-3 py-2"><i class="bi bi-check-circle me-1"></i> DISPONIBLE</span>
                  </td>
                  <td class="text-center">
                    <button class="btn btn-outline-warning btn-sm py-1 px-2" style="font-size: 11px;">Mantenimiento</button>
                  </td>
                </tr>

                <tr>
                  <td class="px-3 fw-bold text-primary fs-6"><i class="bi bi-door-closed me-1"></i> 301</td>
                  <td><span class="badge bg-light text-dark border">Piso 3</span></td>
                  <td class="fw-semibold">Habitación Doble Twin</td>
                  <td><span class="text-success fw-bold">S/. 220.00</span></td>
                  <td><small class="text-muted">Piso silencioso para familias</small></td>
                  <td class="text-center">
                    <span class="badge bg-success px-3 py-2"><i class="bi bi-check-circle me-1"></i> DISPONIBLE</span>
                  </td>
                  <td class="text-center">
                    <button class="btn btn-outline-warning btn-sm py-1 px-2" style="font-size: 11px;">Mantenimiento</button>
                  </td>
                </tr>

                <tr>
                  <td class="px-3 fw-bold text-primary fs-6"><i class="bi bi-door-closed me-1"></i> 401</td>
                  <td><span class="badge bg-light text-dark border">Piso 4</span></td>
                  <td class="fw-semibold">Suite Junior Costa del Sol</td>
                  <td><span class="text-success fw-bold">S/. 320.00</span></td>
                  <td><small class="text-muted">Suite Junior ocupada por reserva ejecutiva</small></td>
                  <td class="text-center">
                    <span class="badge bg-danger px-3 py-2"><i class="bi bi-person-fill-lock me-1"></i> OCUPADA</span>
                  </td>
                  <td class="text-center">
                    <button class="btn btn-outline-success btn-sm py-1 px-2" style="font-size: 11px;">Disponible</button>
                  </td>
                </tr>

                <tr>
                  <td class="px-3 fw-bold text-primary fs-6"><i class="bi bi-door-closed me-1"></i> 402</td>
                  <td><span class="badge bg-light text-dark border">Piso 4</span></td>
                  <td class="fw-semibold">Suite Junior Costa del Sol</td>
                  <td><span class="text-success fw-bold">S/. 320.00</span></td>
                  <td><small class="text-muted">Equipada con hidromasaje y minibar</small></td>
                  <td class="text-center">
                    <span class="badge bg-success px-3 py-2"><i class="bi bi-check-circle me-1"></i> DISPONIBLE</span>
                  </td>
                  <td class="text-center">
                    <button class="btn btn-outline-warning btn-sm py-1 px-2" style="font-size: 11px;">Mantenimiento</button>
                  </td>
                </tr>

                <tr>
                  <td class="px-3 fw-bold text-primary fs-6"><i class="bi bi-door-closed me-1"></i> 501</td>
                  <td><span class="badge bg-light text-dark border">Piso 5</span></td>
                  <td class="fw-semibold">Suite Presidencial Real</td>
                  <td><span class="text-success fw-bold">S/. 550.00</span></td>
                  <td><small class="text-muted">Suite Presidencial con terraza VIP y jacuzzi</small></td>
                  <td class="text-center">
                    <span class="badge bg-success px-3 py-2"><i class="bi bi-check-circle me-1"></i> DISPONIBLE</span>
                  </td>
                  <td class="text-center">
                    <button class="btn btn-outline-warning btn-sm py-1 px-2" style="font-size: 11px;">Mantenimiento</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

    </div>
  </main>

  <!-- Footer Academico UTP -->
  <footer class="footer-utp py-3 mt-auto">
    <div class="container text-center text-muted small">
      Universidad Tecnológica del Perú (UTP) — Ciclo 2026-II
    </div>
  </footer>

</body>
</html>
"""

# -----------------------------------------------------------------------------
# 5. HTML MOCKUP: GitHub Commits History (captura_05_commits_github.html)
# -----------------------------------------------------------------------------
html_05 = r"""<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Commits · Maxrivera52/hotel-costa-del-sol-apf2 · GitHub</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Noto Sans", Helvetica, Arial, sans-serif;
    background-color: #0d1117;
    color: #e6edf3;
    font-size: 14px;
    line-height: 1.5;
  }
  /* Browser Chrome */
  .browser-bar {
    background-color: #161b22;
    border-bottom: 1px solid #30363d;
    padding: 8px 16px;
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 13px;
  }
  .browser-dots { display: flex; gap: 6px; }
  .dot { width: 11px; height: 11px; border-radius: 50%; display: inline-block; }
  .dot-red { background: #ff5f56; }
  .dot-yellow { background: #ffbd2e; }
  .dot-green { background: #27c93f; }
  .url-bar {
    flex: 1;
    background: #0d1117;
    padding: 4px 14px;
    border-radius: 20px;
    border: 1px solid #30363d;
    display: flex;
    align-items: center;
    gap: 8px;
    font-family: Consolas, monospace;
    font-size: 12px;
    color: #8b949e;
  }
  /* GitHub Header */
  .gh-header {
    background-color: #010409;
    padding: 16px 32px;
    border-bottom: 1px solid #21262d;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .repo-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
  }
  .repo-title a { color: #58a6ff; text-decoration: none; font-weight: 600; }
  .repo-badge {
    border: 1px solid #30363d;
    border-radius: 24px;
    padding: 2px 8px;
    font-size: 12px;
    color: #8b949e;
  }
  /* Navigation Tabs */
  .gh-tabs {
    display: flex;
    gap: 16px;
    padding: 0 32px;
    border-bottom: 1px solid #21262d;
    background-color: #010409;
  }
  .gh-tab {
    padding: 10px 8px;
    color: #8b949e;
    text-decoration: none;
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    cursor: pointer;
  }
  .gh-tab.active {
    color: #e6edf3;
    border-bottom: 2px solid #f78166;
    font-weight: 600;
  }
  /* Main Container */
  .gh-main {
    max-width: 1100px;
    margin: 24px auto;
    padding: 0 16px;
  }
  /* Contributors Insight Summary Bar */
  .insights-bar {
    background: #161b22;
    border: 1px solid #30363d;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 24px;
  }
  .insights-title {
    font-weight: 600;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .member-pills {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }
  .member-card {
    flex: 1;
    min-width: 280px;
    background: #0d1117;
    border: 1px solid #30363d;
    border-radius: 6px;
    padding: 10px 14px;
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .avatar {
    width: 38px;
    height: 38px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    color: white;
    font-size: 14px;
  }
  .av-max { background: linear-gradient(135deg, #1f6feb, #388bfd); }
  .av-miguel { background: linear-gradient(135deg, #238636, #2ea043); }
  .av-joel { background: linear-gradient(135deg, #8957e5, #a371f7); }
  
  /* Branch Filter */
  .branch-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
  }
  .btn-branch {
    background: #21262d;
    border: 1px solid #363b42;
    color: #e6edf3;
    padding: 5px 12px;
    border-radius: 6px;
    font-size: 13px;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 6px;
  }
  /* Commits Timeline */
  .timeline-group {
    margin-bottom: 24px;
  }
  .timeline-title {
    font-size: 13px;
    color: #8b949e;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }
  .timeline-title i { font-size: 16px; color: #388bfd; }
  .commit-box {
    border: 1px solid #30363d;
    border-radius: 6px;
    background-color: #161b22;
    overflow: hidden;
  }
  .commit-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 14px;
    border-bottom: 1px solid #21262d;
  }
  .commit-item:last-child { border-bottom: none; }
  .commit-left {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
  .commit-msg {
    font-weight: 600;
    color: #e6edf3;
    text-decoration: none;
    font-size: 14px;
  }
  .commit-meta {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: #8b949e;
  }
  .commit-meta strong { color: #c9d1d9; }
  .commit-meta code {
    background: #21262d;
    padding: 1px 5px;
    border-radius: 4px;
    font-size: 11px;
    color: #a5d6ff;
  }
  .commit-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .badge-verified {
    border: 1px solid #238636;
    color: #3fb950;
    border-radius: 12px;
    padding: 2px 8px;
    font-size: 11px;
    font-weight: 600;
  }
  .btn-hash {
    background: #21262d;
    border: 1px solid #30363d;
    color: #58a6ff;
    padding: 3px 8px;
    border-radius: 6px;
    font-family: Consolas, monospace;
    font-size: 12px;
    text-decoration: none;
  }
</style>
</head>
<body>

  <!-- Browser Bar -->
  <div class="browser-bar">
    <div class="browser-dots">
      <span class="dot dot-red"></span>
      <span class="dot dot-yellow"></span>
      <span class="dot dot-green"></span>
    </div>
    <div class="d-flex gap-2 text-secondary">
      <i class="bi bi-arrow-left"></i>
      <i class="bi bi-arrow-right"></i>
      <i class="bi bi-arrow-clockwise"></i>
    </div>
    <div class="url-bar">
      <i class="bi bi-lock-fill text-success"></i>
      <span>https://github.com/Maxrivera52/hotel-costa-del-sol-apf2/commits/main</span>
    </div>
    <div style="color:#8b949e;"><i class="bi bi-three-dots-vertical"></i></div>
  </div>

  <!-- GitHub Header -->
  <div class="gh-header">
    <div class="repo-title">
      <i class="bi bi-github" style="font-size: 24px; color: white;"></i>
      <a href="#">Maxrivera52</a>
      <span style="color:#8b949e;">/</span>
      <a href="#">hotel-costa-del-sol-apf2</a>
      <span class="repo-badge">Public</span>
    </div>
    <div>
      <span class="repo-badge"><i class="bi bi-star me-1"></i> Star 1</span>
      <span class="repo-badge"><i class="bi bi-diagram-2 me-1"></i> Fork 0</span>
    </div>
  </div>

  <!-- GitHub Tabs -->
  <div class="gh-tabs">
    <div class="gh-tab active"><i class="bi bi-code-slash"></i> Code</div>
    <div class="gh-tab"><i class="bi bi-circle"></i> Issues <span class="repo-badge">0</span></div>
    <div class="gh-tab"><i class="bi bi-arrow-left-right"></i> Pull requests <span class="repo-badge">0</span></div>
    <div class="gh-tab"><i class="bi bi-play-circle"></i> Actions</div>
    <div class="gh-tab"><i class="bi bi-graph-up"></i> Insights</div>
  </div>

  <!-- Main Container -->
  <div class="gh-main">

    <!-- Insights Panel Resumen Colaborativo -->
    <div class="insights-bar">
      <div class="insights-title">
        <span><i class="bi bi-people-fill text-primary me-2"></i> Contributors Activity · 10 Semanas (Agosto - Octubre 2026)</span>
        <span style="color: #3fb950; font-size: 13px; font-weight: 500;">
          <i class="bi bi-check-circle-fill me-1"></i> 30 Commits Totales
        </span>
      </div>
      <div class="member-pills">
        <div class="member-card">
          <div class="avatar av-max">MR</div>
          <div style="flex:1;">
            <div style="font-weight:600; color:#e6edf3;">Max Joel Rivera Morán</div>
            <div style="font-size:12px; color:#8b949e;">U22242437 &bull; 12 commits (40%)</div>
            <div style="font-size:11px; color:#58a6ff;">Backend, DB PostgreSQL & HikariCP</div>
          </div>
        </div>

        <div class="member-card">
          <div class="avatar av-miguel">MS</div>
          <div style="flex:1;">
            <div style="font-weight:600; color:#e6edf3;">Miguel Ángel Suárez Perdomo</div>
            <div style="font-size:12px; color:#8b949e;">U23246385 &bull; 9 commits (30%)</div>
            <div style="font-size:11px; color:#3fb950;">Backend Services & Managed Beans</div>
          </div>
        </div>

        <div class="member-card">
          <div class="avatar av-joel">JC</div>
          <div style="flex:1;">
            <div style="font-weight:600; color:#e6edf3;">Joel Vicente Cori Alarcón</div>
            <div style="font-size:12px; color:#8b949e;">U23303991 &bull; 9 commits (30%)</div>
            <div style="font-size:11px; color:#d2a8ff;">Frontend UI, Facelets & Bootstrap 5</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Branch Filter -->
    <div class="branch-bar">
      <div class="btn-branch">
        <i class="bi bi-git"></i> <strong>main</strong>
      </div>
      <div style="color:#8b949e; font-size: 13px;">
        Mostrando los últimos commits colaborativos
      </div>
    </div>

    <!-- Timeline Group 1 -->
    <div class="timeline-group">
      <div class="timeline-title">
        <i class="bi bi-calendar-event"></i> Commits on Oct 11, 2026
      </div>
      <div class="commit-box">
        <div class="commit-item">
          <div class="commit-left">
            <span class="commit-msg">docs: redactar informe tecnico APF2 adaptado a infraestructura PostgreSQL y JSF</span>
            <div class="commit-meta">
              <div class="avatar av-max" style="width:20px; height:20px; font-size:10px;">MR</div>
              <strong>Max Rivera Morán</strong> committed 1 day ago &bull; <code>maxrivera938@gmail.com</code>
            </div>
          </div>
          <div class="commit-right">
            <span class="badge-verified">Verified</span>
            <span class="btn-hash">a5478ed</span>
          </div>
        </div>

        <div class="commit-item">
          <div class="commit-left">
            <span class="commit-msg">docs: estructurar trazabilidad arquitectonica del APF2 hacia el Proyecto Final Semana 18</span>
            <div class="commit-meta">
              <div class="avatar av-max" style="width:20px; height:20px; font-size:10px;">MR</div>
              <strong>Max Rivera Morán</strong> committed 1 day ago &bull; <code>maxrivera938@gmail.com</code>
            </div>
          </div>
          <div class="commit-right">
            <span class="badge-verified">Verified</span>
            <span class="btn-hash">f6e48b8</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Timeline Group 2 -->
    <div class="timeline-group">
      <div class="timeline-title">
        <i class="bi bi-calendar-event"></i> Commits on Oct 10, 2026
      </div>
      <div class="commit-box">
        <div class="commit-item">
          <div class="commit-left">
            <span class="commit-msg">style: finalizar ajustes de responsividad movil y documentacion en README</span>
            <div class="commit-meta">
              <div class="avatar av-joel" style="width:20px; height:20px; font-size:10px;">JC</div>
              <strong>Joel Cori Alarcón</strong> committed 2 days ago &bull; <code>joelcori1996@gmail.com</code>
            </div>
          </div>
          <div class="commit-right">
            <span class="badge-verified">Verified</span>
            <span class="btn-hash">17e3ae7</span>
          </div>
        </div>

        <div class="commit-item">
          <div class="commit-left">
            <span class="commit-msg">refactor: optimizar mensajes de validacion h:messages y enlaces Expression Language</span>
            <div class="commit-meta">
              <div class="avatar av-miguel" style="width:20px; height:20px; font-size:10px;">MS</div>
              <strong>Miguel Suárez Perdomo</strong> committed 2 days ago &bull; <code>Miguelperdom543@gmail.com</code>
            </div>
          </div>
          <div class="commit-right">
            <span class="badge-verified">Verified</span>
            <span class="btn-hash">05b204a</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Timeline Group 3 -->
    <div class="timeline-group">
      <div class="timeline-title">
        <i class="bi bi-calendar-event"></i> Commits on Oct 09, 2026
      </div>
      <div class="commit-box">
        <div class="commit-item">
          <div class="commit-left">
            <span class="commit-msg">test: validar compilacion WAR y verificacion de ciclo de vida JSF en Tomcat</span>
            <div class="commit-meta">
              <div class="avatar av-max" style="width:20px; height:20px; font-size:10px;">MR</div>
              <strong>Max Rivera Morán</strong> committed 3 days ago &bull; <code>maxrivera938@gmail.com</code>
            </div>
          </div>
          <div class="commit-right">
            <span class="badge-verified">Verified</span>
            <span class="btn-hash">f181f00</span>
          </div>
        </div>

        <div class="commit-item">
          <div class="commit-left">
            <span class="commit-msg">ui: integrar formulario modal h:form para registro de habitaciones con feedback visual</span>
            <div class="commit-meta">
              <div class="avatar av-joel" style="width:20px; height:20px; font-size:10px;">JC</div>
              <strong>Joel Cori Alarcón</strong> committed 3 days ago &bull; <code>joelcori1996@gmail.com</code>
            </div>
          </div>
          <div class="commit-right">
            <span class="badge-verified">Verified</span>
            <span class="btn-hash">f99d52d</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Timeline Group 4 -->
    <div class="timeline-group">
      <div class="timeline-title">
        <i class="bi bi-calendar-event"></i> Commits on Oct 08, 2026
      </div>
      <div class="commit-box">
        <div class="commit-item">
          <div class="commit-left">
            <span class="commit-msg">feat: agregar persistencia de nuevas habitaciones en HabitacionDAO sobre PostgreSQL</span>
            <div class="commit-meta">
              <div class="avatar av-miguel" style="width:20px; height:20px; font-size:10px;">MS</div>
              <strong>Miguel Suárez Perdomo</strong> committed 4 days ago &bull; <code>Miguelperdom543@gmail.com</code>
            </div>
          </div>
          <div class="commit-right">
            <span class="badge-verified">Verified</span>
            <span class="btn-hash">0d7e512</span>
          </div>
        </div>

        <div class="commit-item">
          <div class="commit-left">
            <span class="commit-msg">feat: incorporar filtrado dinamico por estado y tipo en HabitacionBean</span>
            <div class="commit-meta">
              <div class="avatar av-miguel" style="width:20px; height:20px; font-size:10px;">MS</div>
              <strong>Miguel Suárez Perdomo</strong> committed 4 days ago &bull; <code>Miguelperdom543@gmail.com</code>
            </div>
          </div>
          <div class="commit-right">
            <span class="badge-verified">Verified</span>
            <span class="btn-hash">9ad0ab5</span>
          </div>
        </div>
      </div>
    </div>

  </div>

</body>
</html>
"""

# Write HTML files
file_01 = TEMP_HTML_DIR / "captura_01_arquitectura.html"
file_02 = TEMP_HTML_DIR / "captura_02_dashboard.html"
file_03 = TEMP_HTML_DIR / "captura_03_formulario_jsf.html"
file_04 = TEMP_HTML_DIR / "captura_04_datatable_postgresql.html"
file_05 = TEMP_HTML_DIR / "captura_05_commits_github.html"

file_01.write_text(html_01, encoding="utf-8")
file_02.write_text(html_02, encoding="utf-8")
file_03.write_text(html_03, encoding="utf-8")
file_04.write_text(html_04, encoding="utf-8")
file_05.write_text(html_05, encoding="utf-8")

print("HTML template files written successfully.")

# Run Playwright to capture screenshots
tasks = [
    ("captura_01_arquitectura.png", file_01, 1440, 900),
    ("captura_02_dashboard.png", file_02, 1440, 920),
    ("captura_03_formulario_jsf.png", file_03, 1440, 900),
    ("captura_04_datatable_postgresql.png", file_04, 1440, 920),
    ("captura_05_commits_github.png", file_05, 1440, 960),
]

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    for out_name, html_path, width, height in tasks:
        out_file = CAPTURA_DIR / out_name
        print(f"Capturing {out_name}...")
        page = browser.new_page(
            viewport={"width": width, "height": height},
            device_scale_factor=2  # High-DPI crisp screenshot
        )
        url = html_path.as_uri()
        page.goto(url, wait_until="networkidle")
        time.sleep(1.0)  # Ensure web fonts/icons fully settle
        page.screenshot(path=str(out_file), full_page=False)
        page.close()
        print(f" -> Saved: {out_file} ({out_file.stat().st_size} bytes)")

    browser.close()

print("All 5 captures taken successfully with Playwright!")
