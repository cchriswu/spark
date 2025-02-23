/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.errors

import java.io.IOException

import org.apache.hadoop.fs.Path

import org.apache.spark.SparkException
import org.apache.spark.shuffle.{FetchFailedException, ShuffleManager}
import org.apache.spark.storage.{BlockId, BlockManagerId, BlockNotFoundException, BlockSavedOnDecommissionedBlockManagerException, RDDBlockId, UnrecognizedBlockId}

/**
 * Object for grouping error messages from (most) exceptions thrown during query execution.
 */
object SparkCoreErrors {
  def rddBlockNotFoundError(blockId: BlockId, id: Int): Throwable = {
    new Exception(s"Could not compute split, block $blockId of RDD $id not found")
  }

  def blockHaveBeenRemovedError(string: String): Throwable = {
    new SparkException(s"Attempted to use $string after its blocks have been removed!")
  }

  def histogramOnEmptyRDDOrContainingInfinityOrNaNError(): Throwable = {
    new UnsupportedOperationException(
      "Histogram on either an empty RDD or RDD containing +/-infinity or NaN")
  }

  def emptyRDDError(): Throwable = {
    new UnsupportedOperationException("empty RDD")
  }

  def pathNotSupportedError(path: String): Throwable = {
    new IOException(s"Path: ${path} is a directory, which is not supported by the " +
      "record reader when `mapreduce.input.fileinputformat.input.dir.recursive` is false.")
  }

  def checkpointRDDBlockIdNotFoundError(rddBlockId: RDDBlockId): Throwable = {
    new SparkException(
      s"""
         |Checkpoint block $rddBlockId not found! Either the executor
         |that originally checkpointed this partition is no longer alive, or the original RDD is
         |unpersisted. If this problem persists, you may consider using `rdd.checkpoint()`
         |instead, which is slower than local checkpointing but more fault-tolerant.
       """.stripMargin.replaceAll("\n", " "))
  }

  def endOfStreamError(): Throwable = {
    new java.util.NoSuchElementException("End of stream")
  }

  def cannotUseMapSideCombiningWithArrayKeyError(): Throwable = {
    new SparkException("Cannot use map-side combining with array keys.")
  }

  def hashPartitionerCannotPartitionArrayKeyError(): Throwable = {
    new SparkException("HashPartitioner cannot partition array keys.")
  }

  def reduceByKeyLocallyNotSupportArrayKeysError(): Throwable = {
    new SparkException("reduceByKeyLocally() does not support array keys")
  }

  def noSuchElementException(): Throwable = {
    new NoSuchElementException()
  }

  def rddLacksSparkContextError(): Throwable = {
    new SparkException("This RDD lacks a SparkContext. It could happen in the following cases: " +
      "\n(1) RDD transformations and actions are NOT invoked by the driver, but inside of other " +
      "transformations; for example, rdd1.map(x => rdd2.values.count() * x) is invalid " +
      "because the values transformation and count action cannot be performed inside of the " +
      "rdd1.map transformation. For more information, see SPARK-5063.\n(2) When a Spark " +
      "Streaming job recovers from checkpoint, this exception will be hit if a reference to " +
      "an RDD not defined by the streaming job is used in DStream operations. For more " +
      "information, See SPARK-13758.")
  }

  def cannotChangeStorageLevelError(): Throwable = {
    new UnsupportedOperationException(
      "Cannot change storage level of an RDD after it was already assigned a level")
  }

  def canOnlyZipRDDsWithSamePartitionSizeError(): Throwable = {
    new SparkException("Can only zip RDDs with same number of elements in each partition")
  }

  def emptyCollectionError(): Throwable = {
    new UnsupportedOperationException("empty collection")
  }

  def countByValueApproxNotSupportArraysError(): Throwable = {
    new SparkException("countByValueApprox() does not support arrays")
  }

  def checkpointDirectoryHasNotBeenSetInSparkContextError(): Throwable = {
    new SparkException("Checkpoint directory has not been set in the SparkContext")
  }

  def invalidCheckpointFileError(path: Path): Throwable = {
    new SparkException(s"Invalid checkpoint file: $path")
  }

  def failToCreateCheckpointPathError(checkpointDirPath: Path): Throwable = {
    new SparkException(s"Failed to create checkpoint path $checkpointDirPath")
  }

  def checkpointRDDHasDifferentNumberOfPartitionsFromOriginalRDDError(
      originalRDDId: Int,
      originalRDDLength: Int,
      newRDDId: Int,
      newRDDLength: Int): Throwable = {
    new SparkException(
      s"""
         |Checkpoint RDD has a different number of partitions from original RDD. Original
         |RDD [ID: $originalRDDId, num of partitions: $originalRDDLength];
         |Checkpoint RDD [ID: $newRDDId, num of partitions: $newRDDLength].
       """.stripMargin.replaceAll("\n", " "))
  }

  def checkpointFailedToSaveError(task: Int, path: Path): Throwable = {
    new IOException("Checkpoint failed: failed to save output of task: " +
      s"$task and final output path does not exist: $path")
  }

  def mustSpecifyCheckpointDirError(): Throwable = {
    new SparkException("Checkpoint dir must be specified.")
  }

  def unrecognizedBlockIdError(name: String): Throwable = {
    new UnrecognizedBlockId(name)
  }

  def taskHasNotLockedBlockError(currentTaskAttemptId: Long, blockId: BlockId): Throwable = {
    new SparkException(s"Task $currentTaskAttemptId has not locked block $blockId for writing")
  }

  def blockDoesNotExistError(blockId: BlockId): Throwable = {
    new SparkException(s"Block $blockId does not exist")
  }

  def cannotSaveBlockOnDecommissionedExecutorError(blockId: BlockId): Throwable = {
    new BlockSavedOnDecommissionedBlockManagerException(blockId)
  }

  def waitingForReplicationToFinishError(e: Throwable): Throwable = {
    new SparkException("Error occurred while waiting for replication to finish", e)
  }

  def unableToRegisterWithExternalShuffleServerError(e: Throwable): Throwable = {
    new SparkException(s"Unable to register with external shuffle server due to : ${e.getMessage}",
      e)
  }

  def waitingForAsyncReregistrationError(e: Throwable): Throwable = {
    new SparkException("Error occurred while waiting for async. reregistration", e)
  }

  def unexpectedShuffleBlockWithUnsupportedResolverError(
      shuffleManager: ShuffleManager,
      blockId: BlockId): Throwable = {
    new SparkException(s"Unexpected shuffle block ${blockId} with unsupported shuffle " +
      s"resolver ${shuffleManager.shuffleBlockResolver}")
  }

  def failToStoreBlockOnBlockManagerError(
      blockManagerId: BlockManagerId,
      blockId: BlockId): Throwable = {
    new SparkException(s"Failure while trying to store block $blockId on $blockManagerId.")
  }

  def readLockedBlockNotFoundError(blockId: BlockId): Throwable = {
    new SparkException(s"Block $blockId was not found even though it's read-locked")
  }

  def failToGetBlockWithLockError(blockId: BlockId): Throwable = {
    new SparkException(s"get() failed for block $blockId even though we held a lock")
  }

  def blockNotFoundError(blockId: BlockId): Throwable = {
    new BlockNotFoundException(blockId.toString)
  }

  def interruptedError(): Throwable = {
    new InterruptedException()
  }

  def blockStatusQueryReturnedNullError(blockId: BlockId): Throwable = {
    new SparkException(s"BlockManager returned null for BlockStatus query: $blockId")
  }

  def unexpectedBlockManagerMasterEndpointResultError(): Throwable = {
    new SparkException("BlockManagerMasterEndpoint returned false, expected true.")
  }

  def failToCreateDirectoryError(path: String, maxAttempts: Int): Throwable = {
    new IOException(
      s"Failed to create directory ${path} with permission 770 after $maxAttempts attempts!")
  }

  def unsupportedOperationError(): Throwable = {
    new UnsupportedOperationException()
  }

  def noSuchElementError(): Throwable = {
    new NoSuchElementException()
  }

  def fetchFailedError(
      bmAddress: BlockManagerId,
      shuffleId: Int,
      mapId: Long,
      mapIndex: Int,
      reduceId: Int,
      message: String,
      cause: Throwable = null): Throwable = {
    new FetchFailedException(bmAddress, shuffleId, mapId, mapIndex, reduceId, message, cause)
  }

  def failToGetNonShuffleBlockError(blockId: BlockId, e: Throwable): Throwable = {
    new SparkException(s"Failed to get block $blockId, which is not a shuffle block", e)
  }
}
