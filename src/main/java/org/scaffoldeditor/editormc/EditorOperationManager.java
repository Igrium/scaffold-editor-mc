package org.scaffoldeditor.editormc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.editormc.ui.controllers.ProgressWindow;
import org.scaffoldeditor.editormc.util.UIUtils;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.operation.DeleteEntityOperation;
import org.scaffoldeditor.scaffold.operation.Operation;
import org.scaffoldeditor.scaffold.operation.PasteEntitiesOperation;
import org.scaffoldeditor.scaffold.util.ClipboardManager;

import javafx.application.Platform;

/**
 * Handles many of the non-administrative functions of the editor. All of these
 * methods <i>could</i> exist in {@link ScaffoldEditor}, but that class is
 * congested enough already.
 */
public class EditorOperationManager {
    /**
     * The parent editor instance.
     */
    public final ScaffoldEditor editor;
    

    public static EditorOperationManager getInstance() {
        return ScaffoldEditor.getInstance().operationManager;
    }

    protected EditorOperationManager(ScaffoldEditor editor) {
        this.editor = editor;
    }

    /**
	 * Run an operation, updating various world and UI elements accordingly. If
	 * there are values in the redo stack, it gets cleared.
	 * 
	 * @param operation Operation to run.
	 * @return Success.
     * @throws IllegalStateException If no level is loaded.
	 */
	public <T> CompletableFuture<T> runOperation(Operation<T> operation) {
        Level level = editor.getLevel();
		if (level == null) {
			throw new IllegalStateException("Operations can only be run when there is a level loaded.");
		}

		return editor.getLevel().getOperationManager().execute(operation).thenApply(val -> {
			updateViewport();
			return val;
		});
	}

    /**
	 * Copy the current selection to the clipboard.
	 */
	public void copySelection() {
        assertLevel();
		List<StackItem> copy = new ArrayList<>();
		for (Entity ent : editor.getLevel().getLevelStack()) {
			if (editor.getSelectedEntities().contains(ent)) {
				copy.add(new StackItem(ent));
			}
		}

		ClipboardManager.getInstance().copyItems(copy);
	}

	/**
	 * Cut the current selection to the clipboard
	 */
	public CompletableFuture<Void> cutSelection() {
		copySelection();
        Level level = editor.getLevel();
		return runOperation(new DeleteEntityOperation(level, editor.getSelectedEntities()));
	}

	/**
	 * Paste the current clipboard into the level.
	 */
	public CompletableFuture<List<StackItem>> paste() {
        assertLevel();
		return runOperation(new PasteEntitiesOperation(editor.getLevel(), editor.getUI().getOutliner().getSelectedGroup()));
	}

    /**
     * Undo the last operation.
     */
    public CompletableFuture<Void> undo() {
		assertLevel();
        return editor.getLevel().getOperationManager().undo().thenRun(this::updateViewport);
    }
	
	public CompletableFuture<Void> redo() {
		assertLevel();
		return editor.getLevel().getOperationManager().redo().thenRun(this::updateViewport);
	}

	/**
	 * Show the progress UI and recompile the block world.
	 * @return A future that completes when the recompile is finished.
	 */
	public CompletableFuture<Void> compileLevel() {
		assertLevel();
		CompletableFuture<Void> future = new CompletableFuture<>();
		Platform.runLater(() -> {
			ProgressWindow window = ProgressWindow.open(editor.getUI().getStage(), "Compiling blocks...");
			editor.getServiceProvider().execute(() -> {
				try {
					editor.getLevel().compileBlockWorld(false, window.getProgressListener());
					future.complete(null);
				} catch (Throwable e) {
					LogManager.getLogger().error("Error compiling world", e);
					future.completeExceptionally(e);
				}
			});
			
			future.whenComplete((val, e) -> {
				Platform.runLater(() -> {
					window.close();
					if (e != null) {
						UIUtils.showError("Error compiling world", e);
					}
				});
			});
		});
		
		return future;
	}

    private void updateViewport() {
        Level level = editor.getLevel();
        level.updateRenderEntities();
		level.quickRecompile();
    }

    private void assertLevel() {
        assertLevel("This action can only be performed with a level loaded.");		
    }

    private void assertLevel(String message) {
        if (editor.getLevel() == null) throw new IllegalStateException(message);
    }
}
