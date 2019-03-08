package ai.skymind.jobs;

/**
 * Type of model evaluation.
 *
 * @author Max Pumperla
 */
// TODO: this needs some explanation.
public enum EvaluationType {
    ROC_BINARY,
    ROC,
    EVALUATION_BINARY,
    EVALUATION,
    REGRESSON_EVALUATION,
    ROC_MULTI_CLASS;
}
