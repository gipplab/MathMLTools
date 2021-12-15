#!/usr/bin/env bash

# Codeclimate is unable to understand Jacoco's multi-module reports.
# This is because the paths of Jacoco's analysis for multiple modules starts from
# the module root (e.g., com/formulasearchengine/...) but Codeclimate needs to
# link the results with the project root (i.e. src/main/java/com/formulasearchengine/...).
# Hence we manually replace all paths in the Jacoco results.

JACOCO_FILE="target/jacoco-report/jacoco.xml"
JACOCO_REPL_FILE="target/jacoco-report/jacoco-fix.xml"

ls "target/jacoco-report"
echo "Start replacing module paths..."
sed -e 's/com\/formulasearchengine\//src\/main\/java\/com\/formulasearchengine\//g' -e 's/<[\/]\?group[^>]*>//g' $JACOCO_FILE > $JACOCO_REPL_FILE
echo "Finished replacing"
ls "target/jacoco-report"